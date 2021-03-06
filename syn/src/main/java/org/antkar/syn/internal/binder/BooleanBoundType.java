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
package org.antkar.syn.internal.binder;

import java.lang.reflect.Field;

import org.antkar.syn.SynNode;
import org.antkar.syn.ValueNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * Primitive <code>boolean</code> bound type.
 */
final class BooleanBoundType extends BoundType {
    static final BoundType INSTANCE = new BooleanBoundType();

    private BooleanBoundType(){}

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        boolean value = extractValue(synNode);
        return value;
    }

    @Override
    BoundType getArrayType(Field field) throws SynBinderException {
        return BooleanArrayBoundType.INSTANCE;
    }

    static boolean extractValue(SynNode synNode) throws SynBinderException {
        ValueNode valueNode = (ValueNode) synNode;
        if (valueNode == null) {
            throw new SynBinderException("Cannot bind null value to a boolean field");
        }
        boolean value = valueNode.getBoolean();
        return value;
    }
}
