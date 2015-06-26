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
import java.util.Collection;

/**
 * A binder for a {@link SynLookup} field.
 */
abstract class LookupBinder {

    private final Field field;
    
    LookupBinder(Field field) {
        this.field = field;
    }
    
    /**
     * Creates a Java value from a collection of objects that satisfy the {@link SynLookup}'s filter.
     */
    abstract Object createValue(Collection<Object> oObjs) throws SynBinderException;

    /**
     * Binds the given collection of Java objects to the field.
     */
    void bind(Object oThis, Collection<Object> oObjs) throws SynBinderException {
        Object value = createValue(oObjs);
        BinderReflectionUtil.setFieldValue(field, oThis, value);
    }

    /**
     * Returns the associated Java field.
     */
    Field getField() {
        return field;
    }
    
    @Override
    public String toString() {
        return field + "(" + getClass().getSimpleName() + ")";
    }
}
