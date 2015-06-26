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
 * Arithmetical unary operator.
 */
abstract class ArithmeticalUnaryOperator extends UnaryOperator {
    ArithmeticalUnaryOperator(String opLiteral) {
        super(opLiteral);
    }

    @Override
    public final RValue evaluate(Value value) throws SynsException {
        Operand operand = value.toOperand();
        OperandType operandType = operand.getType();
        
        //Evaluate the result depending on the operand type.
        RValue result;
        if (operandType == OperandType.BOOLEAN) {
            boolean v = operand.booleanValue();
            boolean res = evaluate(v);
            result = Value.forBoolean(res);
        } else if (operandType == OperandType.LONG) {
            long v = operand.longValue();
            long res = evaluate(v);
            result = Value.forLong(res);
        } else if (operandType == OperandType.DOUBLE) {
            double v = operand.doubleValue();
            double res = evaluate(v);
            result = Value.forDouble(res);
        } else {
            throw errOperandTypeMissmatch(operandType);
        }
        
        return result;
    }
    
    /**
     * Evaluates the result for a boolean operand.
     */
    boolean evaluate(boolean value) throws SynsException {
        throw errOperandTypeMissmatch(OperandType.BOOLEAN);
    }
    
    /**
     * Evaluates the result for an integer operand.
     */
    long evaluate(long value) throws SynsException {
        throw errOperandTypeMissmatch(OperandType.LONG);
    }
    
    /**
     * Evaluates the result for a floating-point operand.
     */
    double evaluate(double value) throws SynsException {
        throw errOperandTypeMissmatch(OperandType.DOUBLE);
    }
}
