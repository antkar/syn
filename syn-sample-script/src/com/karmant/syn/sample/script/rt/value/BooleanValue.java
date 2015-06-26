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
 * A value of type <code>boolean</code>.
 */
class BooleanValue extends RValue {
    private static RValue TRUE = new BooleanValue(true);
    private static RValue FALSE = new BooleanValue(false);
    
    private final boolean value;
    
    private BooleanValue(boolean value) {
        this.value = value;
    }
    
    static RValue valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }
    
    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forBoolean(value);
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.BOOLEAN;
    }
    
    @Override
    public Object toJava(Class<?> type, TypeMatchPrecision precision) {
        if (boolean.class.equals(type)) {
            precision.increment(2);
            return value;
        } else if (type.isAssignableFrom(Boolean.class)) {
            return value;
        }
        return INVALID;
    }
}
