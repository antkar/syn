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
package com.karmant.syn.sample.script.rt.value;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.javacls.TypeMatchPrecision;
import com.karmant.syn.sample.script.rt.op.operand.Operand;

/**
 * A value of type <code>double</code>.
 */
class DoubleValue extends RValue {
    private final double value;

    DoubleValue(double value) {
        this.value = value;
    }
    
    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forDouble(value);
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.DOUBLE;
    }

    @Override
    public Object toJava(Class<?> type, TypeMatchPrecision precision) {
        if (double.class.equals(type)) {
            precision.increment(2);
            return Double.valueOf(value);
        } else if (type.isAssignableFrom(Double.class)) {
            return value;
        }
        return INVALID;
    }
}
