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
 * Object value node. Contains a value of type {@link Object}.
 */
public final class ObjectValueNode extends ValueNode {
    private final Object value;

    public ObjectValueNode(Object value) {
        super(null);
        this.value = Checks.notNull(value);
    }

    @Override
    public SynValueType getValueType() {
        return SynValueType.OBJECT;
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
