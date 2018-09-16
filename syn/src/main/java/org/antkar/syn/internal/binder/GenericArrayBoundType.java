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

import java.lang.reflect.Array;

import org.antkar.syn.ArrayNode;
import org.antkar.syn.SynNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * A bound type that produces an array of an arbitrary non-primitive Java type.
 *
 * @param <T> the type of the array's element.
 */
final class GenericArrayBoundType<T> extends ArrayBoundType {
    private final BoundType elementType;
    private final Class<?> componentType;

    private GenericArrayBoundType(BoundType elementType, Class<?> componentType) {
        this.elementType = elementType;
        this.componentType = componentType;
    }

    /**
     * Returns an instance of a generic array bound type.
     */
    static <T> GenericArrayBoundType<T> create(BoundType elementType, Class<T> componentType) {
        //Now simply creates a new object, but can be changed to cache instances.
        return new GenericArrayBoundType<>(elementType, componentType);
    }

    @Override
    Object convertArray(
            BinderEngine<?> engine,
            BoundObject bObjOwner,
            String key,
            ArrayNode arrayNode,
            int size) throws SynBinderException
    {
        Object[] array = (Object[])Array.newInstance(componentType, size);

        for (int i = 0; i < size; ++i) {
            SynNode synElementNode = arrayNode.get(i);
            Object value = elementType.convertNode(engine, synElementNode, bObjOwner, key);
            array[i] = value;
        }

        return array;
    }
}
