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
package org.antkar.syn.sample.script.rt.op;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.rt.op.operand.OperandType;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Equality operator - either <code>==</code> or <code>!=</code>.
 */
public abstract class EqNeBinaryOperator extends BinaryOperator {
    EqNeBinaryOperator(String opLiteral) {
        super(opLiteral);
    }
    
    @Override
    public final RValue evaluate(Operand left, Operand right) throws SynsException {
        OperandType leftType = left.getType();
        OperandType rightType = right.getType();
        
        //If one of the operands is null while the other one is not, return false independently
        //from the type of the other operand.
        if (leftType != rightType && (leftType == OperandType.NULL || rightType == OperandType.NULL)) {
            return makeResult(false);
        }
        
        //If both operands are references (null, String, Object), compare references.
        if (isReference(leftType) && isReference(rightType)) {
            Object leftObj = left.objectValue();
            Object rightObj = right.objectValue();
            boolean equal = leftObj == rightObj;
            return makeResult(equal);
        }
        
        return super.evaluate(left, right);
    }

    @Override
    final RValue evaluate(boolean left, boolean right) {
        return makeResult(left == right);
    }

    @Override
    final RValue evaluate(long left, long right) {
        return makeResult(left == right);
    }

    @Override
    final RValue evaluate(double left, double right) {
        return makeResult(left == right);
    }
    
    /**
     * Returns the result of the operator in the form of {@link RValue} depending on the equality
     * of the operands.
     */
    private RValue makeResult(boolean equal) {
        boolean result = evaluateResult(equal);
        return Value.forBoolean(result);
    }
    
    /**
     * Evaluates the <code>boolean</code> result depending on the equality of operands.
     */
    abstract boolean evaluateResult(boolean equal);
    
    private static boolean isReference(OperandType type) {
        return OperandType.NULL == type || OperandType.STRING == type || OperandType.OBJECT == type;
    }
}
