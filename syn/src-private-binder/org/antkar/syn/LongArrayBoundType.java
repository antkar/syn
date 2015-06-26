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

import org.antkar.syn.ArrayNode;
import org.antkar.syn.SynBinderException;
import org.antkar.syn.SynNode;

/**
 * Primitive <code>long</code> array bound type.
 */
class LongArrayBoundType extends ArrayBoundType {
    static final BoundType INSTANCE = new LongArrayBoundType();

    private LongArrayBoundType(){}

    @Override
    Object convertArray(
            BinderEngine<?> engine,
            BoundObject bObjOwner,
            String key,
            ArrayNode arrayNode,
            int size) throws SynBinderException
    {
        long[] array = new long[size];
        
        for (int i = 0; i < size; ++i) {
            SynNode synElementNode = arrayNode.get(i);
            long value = LongBoundType.extractValue(synElementNode);
            array[i] = value;
        }
        
        return array;
    }
}
