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

import org.antkar.syn.ArrayNode;
import org.antkar.syn.SynNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * Primitive <code>int</code> array bound type.
 */
class IntArrayBoundType extends ArrayBoundType {
    static final BoundType INSTANCE = new IntArrayBoundType();

    private IntArrayBoundType(){}

    @Override
    Object convertArray(
            BinderEngine<?> engine,
            BoundObject bObjOwner,
            String key,
            ArrayNode arrayNode,
            int size) throws SynBinderException
    {
        int[] array = new int[size];
        
        for (int i = 0; i < size; ++i) {
            SynNode synElementNode = arrayNode.get(i);
            int value = IntBoundType.extractValue(synElementNode);
            array[i] = value;
        }
        
        return array;
    }
}
