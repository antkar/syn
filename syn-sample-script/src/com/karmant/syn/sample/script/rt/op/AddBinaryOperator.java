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
package com.karmant.syn.sample.script.rt.op;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.op.operand.Operand;
import com.karmant.syn.sample.script.rt.op.operand.OperandType;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Script Language <code>+</code> operator.
 */
class AddBinaryOperator extends BinaryOperator {
    AddBinaryOperator() {
        super("+");
    }

    @Override
    public RValue evaluate(Operand left, Operand right) throws SynsException {
        OperandType leftType = left.getType();
        OperandType rightType = right.getType();
        if (leftType == OperandType.STRING || rightType == OperandType.STRING) {
            return evaluateAddString(left, right);
        }
        return super.evaluate(left, right);
    }
    
    /**
     * Evaluates string concatenation.
     */
    private RValue evaluateAddString(Operand left, Operand right) throws SynsException {
        String leftStr = left.stringValue();
        String rightStr = right.stringValue();
        String result = leftStr + rightStr;
        return Value.forString(result);
    }

    @Override
    RValue evaluate(long left, long right) {
        return Value.forLong(left + right);
    }

    @Override
    RValue evaluate(double left, double right) {
        return Value.forDouble(left + right);
    }
}
