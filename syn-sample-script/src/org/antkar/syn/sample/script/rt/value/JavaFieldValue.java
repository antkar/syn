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
package org.antkar.syn.sample.script.rt.value;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.javacls.JavaField;
import org.antkar.syn.sample.script.rt.javacls.TypeMatchPrecision;

/**
 * A Java field value. Allows reading and writing fields of Java objects.
 */
abstract class JavaFieldValue extends LValue {
    private final JavaField field;

    JavaFieldValue(JavaField field) {
        this.field = field;
    }
    
    /**
     * Returns the associated Java field.
     */
    final JavaField getField() {
        return field;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.JAVAFIELD;
    }
    
    @Override
    ValueType getTypeofValueType() throws SynsException {
        return toRValue().getTypeofValueType();
    }
    
    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(getField() + "");
    }

    @Override
    public void assign(RValue value) throws SynsException {
        Object object = value.toJava(field.getType(), TypeMatchPrecision.NULL);
        setFieldValue(object);
    }

    @Override
    public RValue toRValue() throws SynsException {
        Object javaValue = getFieldValue();
        return Value.forJavaObject(javaValue);
    }
    
    /**
     * Reads the value of the Java field.
     */
    abstract Object getFieldValue() throws SynsException;
    
    /**
     * Writes the value of the Java field.
     */
    abstract void setFieldValue(Object value) throws SynsException;
}
