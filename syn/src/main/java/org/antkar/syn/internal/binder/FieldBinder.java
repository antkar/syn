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

import org.antkar.syn.ObjectNode;
import org.antkar.syn.SynNode;
import org.antkar.syn.binder.SynBinderException;

/**
 * Field binder. Knows how to bind the value of a grammar attribute to a Java field.
 */
class FieldBinder {

    /** Grammar attribute name. */
    private final String attribute;
    
    /** Java field. */
    private final Field field;
    
    /** <code>true</code> if the grammar attribute is embedded. */
    private final boolean embedded;
    
    /** Bound type. */
    private final BoundType type;
    
    FieldBinder(String attribute, Field field, boolean embedded, BoundType type) {
        this.attribute = attribute;
        this.field = field;
        this.embedded = embedded;
        this.type = type;
    }

    /**
     * Binds an attribute of the passed SYN object node to a field of a Java object.
     */
    final void bind(BinderEngine<?> engine, ObjectNode objNode, BoundObject bObj) throws SynBinderException {
        SynNode synNode = objNode.get(attribute);
        if (synNode == null && !objNode.containsKey(attribute) && !embedded) {
            //If the attribute is embedded, it is allowed to leave it uninitialized, because the
            //attribute might have been defined in an optional element.
            throw new IllegalStateException(String.format("Attribute is not defined: %s", attribute));
        }
        
        if (synNode != null) {
            Object value = convetNodeToValue(engine, bObj, synNode);
            setFieldValue(bObj, value);
        }
    }

    /**
     * Converts a SYN node to a Java object.
     */
    private Object convetNodeToValue(BinderEngine<?> engine, BoundObject bObj, SynNode synNode)
            throws SynBinderException
    {
        try {
            Object value = type.convertNode(engine, synNode, bObj, attribute);
            return value;
        } catch (SynBinderException e) {
            throw new SynBinderException(e.getMessage() + " (" + field + ")", e);
        }
    }

    /**
     * Assigns a value to a field of a bound object.
     */
    private void setFieldValue(BoundObject bObj, Object value) throws SynBinderException {
        Object obj = bObj.getJavaObject();
        BinderReflectionUtil.setFieldValue(field, obj, value);
    }
    
    /**
     * Returns the Java field associated with this binder.
     */
    Field getField() {
        return field;
    }
    
    /**
     * Returns the Java field name.
     */
    String getFieldName() {
        String name = field.getName();
        return name;
    }

    /**
     * Returns <code>true</code> if this field binder is compatible with the passed one. Used to check
     * whether two field binders are in a conflict in the case when an attribute with the same name is defined
     * more than once in different embedded productions.
     */
    boolean isTheSame(FieldBinder otherBinder) {
        Class<? extends FieldBinder> class1 = getClass();
        Class<? extends FieldBinder> class2 = otherBinder.getClass();
        boolean equals = class1.equals(class2);
        return equals;
    }
    
    @Override
    public String toString() {
        return field + "(" + getClass().getSimpleName() + ")";
    }
}
