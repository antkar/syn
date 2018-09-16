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
import org.antkar.syn.sample.script.rt.javacls.TypeMatchPrecision;

/**
 * An r-value. A value that cannot be modified.
 */
public abstract class RValue extends Value {
    public static final Object INVALID = new Object();

    static final RValue[] ARRAY0 = {};

    RValue(){}

    @Override
    public final RValue toRValue() {
        return this;
    }

    /**
     * Converts this value to a Java value of the specified type.
     *
     * @param type Java type.
     * @param precision Conversion precision.
     *
     * @return {@link #INVALID}, if the value is not compatible with the type.
     *
     * @throws SynsException on error.
     */
    public Object toJava(Class<?> type, TypeMatchPrecision precision) throws SynsException {
        throw errInvalidOperation();
    }

    /**
     * Returns the element of the array with the specified index. Throws an exception if this is not
     * an array value.
     *
     * @param index Index of element.
     *
     * @throws SynsException on error.
     */
    public LValue getArrayElement(int index) throws SynsException {
        throw errInvalidOperation();
    }

    /**
     * Returns an {@link Iterable} which can be used by a <code>for</code> statement to iterate over the
     * elements of a collection. Throws an exception if this value is not a collection.
     */
    public Iterable<RValue> toIterable() throws SynsException {
        throw errInvalidOperation();
    }

    @Override
    public String toString() {
        try {
            return toOperand().stringValue();
        } catch (SynsException e) {
            throw new RuntimeException(e);
        }
    }
}
