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
import java.util.ArrayList;
import java.util.List;

import org.antkar.syn.ArrayNode;
import org.antkar.syn.SynNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * {@link List} bound type.
 */
final class ListBoundType extends BoundType {
    private final BoundType elementType;

    ListBoundType(BoundType elementType) {
        this.elementType = elementType;
    }

    @Override
    BoundType getArrayType(Field field) throws SynBinderException {
        throw new SynBinderException(String.format("Arrays of List are not supported (%s)", field));
    }

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        ArrayNode arrayNode = (ArrayNode) synNode;
        int size = arrayNode.size();
        List<Object> list = new ArrayList<>(size);

        for (int i = 0; i < size; ++i) {
            SynNode synElementNode = arrayNode.get(i);
            Object value = elementType.convertNode(engine, synElementNode, bObjOwner, key);
            list.add(value);
        }

        return list;
    }
}
