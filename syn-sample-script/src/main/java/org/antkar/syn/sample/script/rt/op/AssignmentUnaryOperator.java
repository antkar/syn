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
import org.antkar.syn.sample.script.rt.value.LValue;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * An unary operator that assigns a new value to its operand. A common superclass for increment
 * and decrement operators.
 */
abstract class AssignmentUnaryOperator extends UnaryOperator {
    AssignmentUnaryOperator(String opLiteral) {
        super(opLiteral);
    }

    @Override
    public final Value evaluate(Value value) throws SynsException {
        LValue lvalue = value.toLValue();
        RValue rOldValue = value.toRValue();
        Operand operand = value.toOperand();
        OperandType operandType = operand.getType();

        //Evaluate the result depending on the type of the operand.
        RValue nextValue;
        if (operandType == OperandType.LONG) {
            long v = operand.longValue();
            long res = evaluate(v);
            nextValue = Value.forLong(res);
        } else if (operandType == OperandType.DOUBLE) {
            double v = operand.doubleValue();
            double res = evaluate(v);
            nextValue = Value.forDouble(res);
        } else {
            throw errOperandTypeMissmatch(operandType);
        }
        
        //Assign the new value to the variable.
        lvalue.assign(nextValue);
        
        //Return either the new or the old value, depending on the operator type - prefix or postfix.
        Value result = resultValue(rOldValue, lvalue);
        return result;
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
    
    /**
     * Chooses the result value for the operator - either the old value of the variable, or the
     * new value.
     */
    abstract Value resultValue(RValue prevValue, LValue nextValue);
}
