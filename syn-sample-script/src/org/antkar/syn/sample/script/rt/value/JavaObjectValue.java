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

import java.util.Iterator;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.javacls.JavaClass;
import org.antkar.syn.sample.script.rt.javacls.TypeMatchPrecision;
import org.antkar.syn.sample.script.rt.op.operand.Operand;

/**
 * Java objet value. Allows to use an arbitrary Java object in a script.
 */
class JavaObjectValue extends RValue {
    private final JavaClass cls;
    private final Object obj;

    JavaObjectValue(Object obj) {
        this.cls = JavaClass.getInstance(obj.getClass());
        this.obj = obj;
    }

    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forObject(this, obj);
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.JAVAOBJECT;
    }
    
    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(obj.getClass().getCanonicalName());
    }
    
    @Override
    public Object toJava(Class<?> type, TypeMatchPrecision precision) {
        if (type.isInstance(obj)) {
            return obj;
        } else {
            return INVALID;
        }
    }
    
    @Override
    public Value getMemberOpt(String name, ScriptScope readerScope) {
        return cls.getInstanceMemberOpt(name, obj);
    }
    
    @Override
    public Iterable<RValue> toIterable() throws SynsException {
        if (obj instanceof Iterable<?>) {
            return new JavaIterableIterable((Iterable<?>)obj);
        }
        throw errInvalidOperation();
    }
    
    @Override
    public String toString() {
        return "" + obj;
    }
    
    /**
     * If the Java object implements the {@link Iterable} interface, this class is used to
     * adapt the object to an {@link Iterable} which returns script values.
     */
    private static final class JavaIterableIterable implements Iterable<RValue> {
        private final Iterable<?> iterable;
        
        JavaIterableIterable(Iterable<?> iterable) {
            this.iterable = iterable;
        }
        
        @Override
        public Iterator<RValue> iterator() {
            Iterator<?> iterator = iterable.iterator();
            return new JavaIteratorIterator(iterator);
        }
    }
    
    /**
     * Adapts an arbitrary Java object iterator an a script values iterator.
     */
    private static final class JavaIteratorIterator implements Iterator<RValue> {
        private final Iterator<?> iterator;

        JavaIteratorIterator(Iterator<?> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public RValue next() {
            Object object = iterator.next();
            try {
                return Value.forJavaObject(object);
            } catch (SynsException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
