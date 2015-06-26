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
package com.karmant.syn;

import java.lang.reflect.Field;

/**
 * Primitive <code>double</code> bound type.
 */
class DoubleBoundType extends BoundType {
    static final BoundType INSTANCE = new DoubleBoundType();

    private DoubleBoundType(){}

    @Override
    Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
            throws SynBinderException
    {
        double value = extractValue(synNode);
        return value;
    }

    @Override
    BoundType getArrayType(Field field) throws SynBinderException {
        return DoubleArrayBoundType.INSTANCE;
    }

    static double extractValue(SynNode synNode) throws SynBinderException {
        ValueNode valueNode = (ValueNode) synNode;
        if (valueNode == null) {
            throw new SynBinderException("Cannot bind null value to a double field");
        }
        double value = valueNode.getFloat();
        return value;
    }
}
