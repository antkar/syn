/*
 * Copyright 2013 Anton Karmanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.antkar.syn.sample.script.schema;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.BinaryOperator;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.rt.value.LValue;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

import org.antkar.syn.SynInit;

/**
 * Assignment expression syntax node.
 */
public class AssignmentExpression extends BinaryExpression {
    /** The associated arithmetical operator for a compound assignment operator or
     * <code>null</code> if this is a simple assignment operator. */
    private BinaryOperator op;
    
    public AssignmentExpression(){}
    
    @SynInit
    private void init() {
        String opLiteral = getOp();
        op = "=".equals(opLiteral) ? null : BinaryOperator.forAssignmentLiteral(opLiteral);
    }

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        //Evaluate operands.
        Value left = getLeft().evaluate(scope);
        LValue lleft = left.toLValue();
        Value right = getRight().evaluate(scope);
        RValue rright = right.toRValue();
        
        //Apply the arithmetical operator, if any.
        RValue result = rright;
        if (op != null) {
            Operand leftOperand = left.toOperand();
            Operand rightOperand = right.toOperand();
            result = op.evaluate(leftOperand, rightOperand);
        }
        
        //Assign the new value to the variable and return that value.
        lleft.assign(result);
        return result;
    }
}
