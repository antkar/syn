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
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

import org.antkar.syn.SynInit;

/**
 * Arithmetical binary expression syntax node.
 */
public abstract class ArithmeticalBinaryExpression extends BinaryExpression {
    /** The operator used in this expression. */
    private BinaryOperator op;
    
    public ArithmeticalBinaryExpression(){}
    
    @SynInit
    private void init() {
        op = BinaryOperator.forLiteral(getOp());
    }

    @Override
    final Value evaluate0(ScriptScope scope) throws SynsException {
        //Calculate the left operand.
        Value left = getLeft().evaluate(scope);
        Operand leftOperand = left.toOperand();
        
        //Try a short-circuit evaluation first.
        RValue result = op.evaluate(leftOperand);
        
        if (result == null) {
            //Now calculate the right operand and run a normal evaluation.
            Value right = getRight().evaluate(scope);
            Operand rightOperand = right.toOperand();
            result = op.evaluate(leftOperand, rightOperand);
        }
        
        return result;
    }
}
