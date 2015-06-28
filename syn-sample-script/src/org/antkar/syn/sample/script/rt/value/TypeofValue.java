/*
 * Copyright 2015 Anton Karmanov
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
package org.antkar.syn.sample.script.rt.value;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;

/**
 * A value returned by the <code>typeof</code> expression.
 */
class TypeofValue extends RValue {
    private static final Map<ValueType, TypeofValue> VALUES;
    
    static {
        Map<ValueType, TypeofValue> values = new EnumMap<>(ValueType.class);
        for (ValueType valueType : ValueType.values()) {
            values.put(valueType, new TypeofValue(valueType));
        }
        VALUES = Collections.unmodifiableMap(values);
    }

    private final ValueType valueType;
    private final Value typeValue;
    
    private TypeofValue(ValueType valueType) {
        this.valueType = valueType;
        typeValue = Value.forString(valueType.getTypeName());
    }
    
    static Value forValueType(ValueType valueType) {
        TypeofValue value = VALUES.get(valueType);
        if (value == null) {
            throw new IllegalStateException("" + valueType);
        }
        return value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.OBJECT;
    }
    
    @Override
    public Value getMemberOpt(String name, ScriptScope readerScope) throws SynsException {
        switch (name) {
        case "type": return typeValue;
        default: return null;
        }
    }
    
    @Override
    public String toString() {
        return valueType.getTypeName();
    }
}
