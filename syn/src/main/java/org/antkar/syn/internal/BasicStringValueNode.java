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
import org.antkar.syn.ValueNode;

/**
 * {@link String}-based value node. Subclasses are {@link IdentifierValueNode} and {@link StringValueNode}.
 */
abstract class BasicStringValueNode extends ValueNode {
    private final String value;

    BasicStringValueNode(PosBuffer pos, String value) {
        super(pos);
        this.value = Checks.notNull(value);
    }

    @Override
    public final SynValueType getValueType() {
        return SynValueType.STRING;
    }

    @Override
    public final String getString() {
        return value;
    }

    @Override
    public final Object getValue() {
        return value;
    }

    @Override
    public final String toString() {
        return getTokenDescriptor() + "(" + getString() + ")";
    }
}
