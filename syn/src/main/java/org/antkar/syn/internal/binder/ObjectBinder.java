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

import java.util.List;

import org.antkar.syn.ObjectNode;
import org.antkar.syn.binder.SynBinderException;
import org.antkar.syn.internal.CommonUtil;

/**
 * Object binder. Describes how to bind attributes of an AST node to fields of a Java object.
 */
final class ObjectBinder {

    private final Class<?> classToBind;
    private final List<FieldBinder> fieldBinders;

    ObjectBinder(Class<?> classToBind, List<FieldBinder> fieldBinders) {
        this.classToBind = classToBind;
        this.fieldBinders = CommonUtil.unmodifiableListCopy(fieldBinders);
    }

    /**
     * Creates a Java object and binds the attributes of the given AST node to its fields.
     */
    BoundObject bindNode(BinderEngine<?> engine, ObjectNode synNode) throws SynBinderException {
        Object obj = createObjectInstance();

        BoundObject bObj = new BoundObject(obj);
        for (FieldBinder fieldBinder : fieldBinders) {
            fieldBinder.bind(engine, synNode, bObj);
        }

        engine.addBObj(bObj);

        return bObj;
    }

    /**
     * Creates a new Java object instance.
     */
    private Object createObjectInstance() throws SynBinderException {
        try {
            return classToBind.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SynBinderException(String.format(
                    "Unable to create an object of class %s",
                    classToBind.getCanonicalName()), e);
        }
    }

    @Override
    public String toString() {
        return classToBind + "";
    }
}
