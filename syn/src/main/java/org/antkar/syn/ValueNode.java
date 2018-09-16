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
package org.antkar.syn;

import org.antkar.syn.internal.PosBuffer;

/**
 * A terminal node that has an associated value, like an identifier or an integer number. The value may come
 * either from the input, or from a constant defined in the grammar.
 */
public abstract class ValueNode extends TerminalNode {

    /**
     * @param pos the position, or <code>null</code> if there is no one associated.
     */
    protected ValueNode(PosBuffer pos) {
        super(pos);
    }

    /**
     * Returns the type of this value.
     * @return the value type. Not <code>null</code>.
     */
    public abstract SynValueType getValueType();

    /**
     * Returns the value as an object. For primitive types (integer, boolean, etc.) returns the corresponding
     * wrapper. For string and object values returns the value itself. Does not return <code>null</code>.
     * @return the value.
     */
    public abstract Object getValue();

    /**
     * Returns the <code>int</code> value. The node must be an integer value node with a value within
     * the <code>int</code> range, otherwise an exception is thrown.
     *
     * @return the <code>int</code> value.
     * @throws UnsupportedOperationException if the node is not an integer node.
     * @throws IllegalStateException if the node is an integer node, but the value is out of
     * <code>int</code> range.
     *
     * @see #getLong()
     */
    public int getInt() {
        throw new UnsupportedOperationException(getValueType().toString());
    }

    /**
     * Returns the <code>long</code> value. The node must be an integer value node.
     *
     * @return the <code>long</code> value.
     * @throws UnsupportedOperationException if the node is not an integer node.
     */
    public long getLong() {
        throw new UnsupportedOperationException(getValueType().toString());
    }

    /**
     * Returns the string value. The node must be a string value node.
     *
     * @return the string value. Not <code>null</code>.
     * @throws UnsupportedOperationException if the node is not a string node.
     */
    @Override
    public String getString() {
        throw new UnsupportedOperationException(getValueType().toString());
    }

    /**
     * Returns the boolean value. The node must be a boolean value node.
     *
     * @return the boolean value.
     * @throws UnsupportedOperationException if the node is not a boolean node.
     */
    public boolean getBoolean() {
        throw new UnsupportedOperationException(getValueType().toString());
    }

    /**
     * Returns the <code>double</code> floating-point value. The node must be a floating-point node.
     *
     * @return the floating-point value.
     * @throws UnsupportedOperationException if the node is not a floating-point node.
     */
    public double getFloat() {
        throw new UnsupportedOperationException(getValueType().toString());
    }

    /**
     * Returns a string representation of this value. The representation contains the value type and the string
     * representation of the object value.
     *
     * @return a string representation.
     */
    @Override
    public String toString() {
        return getValueType() + "(" + getValue() + ")";
    }
}
