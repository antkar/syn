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

import org.antkar.syn.ArrayNode;
import org.antkar.syn.SynNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * Common superclass for primitive array bound types.
 */
abstract class ArrayBoundType extends BoundType {
    ArrayBoundType(){}

    @Override
    final BoundType getArrayType(Field field) throws SynBinderException {
        throw new SynBinderException(String.format("Multidimensional arrays are not supported (%s)", field));
    }

    @Override
    final Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        ArrayNode arrayNode = (ArrayNode) synNode;
        int size = arrayNode.size();
        return convertArray(engine, bObjOwner, key, arrayNode, size);
    }
    
    /**
     * Converts an array node to a Java array value.
     */
    abstract Object convertArray(
            BinderEngine<?> engine,
            BoundObject bObjOwner,
            String key,
            ArrayNode arrayNode,
            int size) throws SynBinderException;
}
