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
import java.util.List;

import org.antkar.syn.SynNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * Describes a type of the value bound to a Java class field.
 */
abstract class BoundType {
    private BoundType listType;

    BoundType(){}

    /**
     * Returns the bound type denoting the array of this type.
     * @throws SynBinderException if the type does not support arrays.
     */
    abstract BoundType getArrayType(Field field) throws SynBinderException;

    /**
     * Returns the bound type denoting the {@link List} of this type.
     * @throws SynBinderException if the type does not support lists.
     */
    final BoundType getListType() throws SynBinderException {
        if (listType == null) {
            listType = new ListBoundType(this);
        }
        return listType;
    }

    /**
     * Converts a SYN node to a value which has to be bound to a Java field.
     * @throws SynBinderException if the node cannot be converted.
     */
    abstract Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException;
}
