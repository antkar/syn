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
package org.antkar.syn.internal;

import org.antkar.syn.SynValueType;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.ValueNode;

/**
 * Boolean value node. Contains a value of type <code>boolean</code>.
 */
public final class BooleanValueNode extends ValueNode {
    /**
     * <code>true</code> value.
     */
    public static final BooleanValueNode TRUE = new BooleanValueNode(true);

    /**
     * <code>false</code> value.
     */
    public static final BooleanValueNode FALSE = new BooleanValueNode(false);

    /**
     * The value. Not <code>null</code>. A wrapper type is used in order to avoid boxing in the
     * {@link #getValue()} method.
     */
    private final Boolean value;

    private BooleanValueNode(boolean value) {
        super(null);
        this.value = value;
    }

    /**
     * Returns the instance for the specified <code>boolean</code> value.
     *
     * @param value the <code>boolean</code> value.
     * @return the instance.
     */
    public static BooleanValueNode getInstance(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public boolean getBoolean() {
        return value;
    }

    @Override
    public SynValueType getValueType() {
        return SynValueType.BOOLEAN;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public TokenDescriptor getTokenDescriptor() {
        return null;
    }
}
