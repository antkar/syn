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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.javacls.TypeMatchPrecision;
import com.karmant.syn.sample.script.rt.op.operand.Operand;

/**
 * Array value.
 */
class ArrayValue extends RValue {
    private final ArrayElementValue[] array;
    
    ArrayValue(RValue[] array0) {
        array = new ArrayElementValue[array0.length];
        for (int i = 0, n = array.length; i < n; ++i) {
            array[i] = new ArrayElementValue(array0[i]);
        }
    }
    
    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forObject(this);
    }
    
    @Override
    public Value getMemberOpt(String name) {
        if ("length".equals(name)) {
            return Value.forInt(array.length);
        }
        return null;
    }
    
    @Override
    public LValue getArrayElement(int index) throws SynsException {
        if (index < 0 || index >= array.length) {
            throw new SynsException("Index out of bounds: " + index);
        }
        return array[index];
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }
    
    @Override
    public Object toJava(Class<?> type, TypeMatchPrecision precision) throws SynsException {
        if (type.isArray()) {
            return toJavaArray(type, precision);
        } else if (type.isAssignableFrom(Collection.class)) {
            return toJavaCollection(precision);
        } else {
            return INVALID;
        }
    }

    /**
     * Converts this value to a Java array.
     */
    private Object toJavaArray(Class<?> type, TypeMatchPrecision precision) throws SynsException {
        Class<?> componentType = type.getComponentType();
        Object result = Array.newInstance(componentType, array.length);
        
        for (int i = 0, n = array.length; i < n; ++i) {
            Object value = array[i].toRValue().toJava(componentType, precision);
            if (value == INVALID) {
                return INVALID;
            }
            Array.set(result, i, value);
        }
        
        return result;
    }

    /**
     * Converts this value to a Java collection.
     */
    private Object toJavaCollection(TypeMatchPrecision precision) throws SynsException {
        List<Object> result = new ArrayList<>(array.length);
        
        for (ArrayElementValue element : array) {
            Object javaElement = element.toRValue().toJava(Object.class, precision);
            if (javaElement == INVALID) {
                return INVALID;
            }
            result.add(javaElement);
        }
        
        return result;
    }
    
    @Override
    public Iterable<RValue> toIterable() {
        return new ArrayIterable();
    }
    
    /**
     * {@link Iterable} interface implementation.
     */
    private class ArrayIterable implements Iterable<RValue> {
        ArrayIterable(){}

        @Override
        public Iterator<RValue> iterator() {
            return new ArrayIterator();
        }
    }
    
    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("[");
        
        String sep = "";
        for (ArrayElementValue value : array) {
            bld.append(sep);
            bld.append(value.toString());
            sep = ", ";
        }
        
        bld.append("]");
        return bld.toString();
    }
    
    /**
     * Iterator.
     */
    private class ArrayIterator implements Iterator<RValue> {
        private int index;
        
        ArrayIterator(){}

        @Override
        public boolean hasNext() {
            return index < array.length;
        }

        @Override
        public RValue next() {
            if (index >= array.length) {
                throw new NoSuchElementException();
            }
            return array[index++].toRValue();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
