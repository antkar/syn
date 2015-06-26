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

import java.lang.reflect.Field;

import org.antkar.syn.SynBinderException;
import org.antkar.syn.SynNode;
import org.antkar.syn.ValueNode;

/**
 * Primitive <code>float</code> bound type.
 */
class FloatBoundType extends BoundType {
    static final BoundType INSTANCE = new FloatBoundType();

    private FloatBoundType(){}

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        float value = extractValue(synNode);
        return value;
    }

    @Override
    BoundType getArrayType(Field field) throws SynBinderException {
        return FloatArrayBoundType.INSTANCE;
    }

    static float extractValue(SynNode synNode) throws SynBinderException {
        ValueNode valueNode = (ValueNode) synNode;
        if (valueNode == null) {
            throw new SynBinderException("Cannot bind null value to a float field");
        }
        float value = (float) valueNode.getFloat();
        return value;
    }
}
