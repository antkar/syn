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
package org.antkar.syn.sample.script.rt.javacls;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.JavaInstanceFieldValue;
import org.antkar.syn.sample.script.rt.value.JavaStaticFieldValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Wraps a Java {@link Field}.
 */
public class JavaField extends JavaMember {
    private final Field field;
    
    JavaField(Field field) {
        this.field = field;
    }
    
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }
    
    public Class<?> getType() {
        return field.getType();
    }
    
    @Override
    Value getStaticValue() throws SynsException {
        if (!isStatic()) {
            throw new SynsException("Cannot statically access instance field " + field);
        }
        
        return new JavaStaticFieldValue(this);
    }

    @Override
    Value getInstanceValue(Object obj) {
        return new JavaInstanceFieldValue(this, obj);
    }

    public Object get(Object obj) throws SynsException {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new SynsException(e);
        }
    }
    
    public void set(Object obj, Object value) throws SynsException {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new SynsException(e);
        }
    }

    @Override
    public String toString() {
        return field.toString();
    }
}
