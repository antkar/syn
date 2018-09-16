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

import java.util.HashMap;
import java.util.Map;

import org.antkar.syn.binder.SynLookup;

/**
 * Describes a Java object bound to a SYN node, along with the information about other such objects
 * referenced by this one. Used by {@link SynLookup} functionality.
 */
public final class BoundObject {

    private final Object javaObject;
    private BoundObject owner;
    private Map<String, BoundObject> references;

    BoundObject(Object javaObject) {
        this.javaObject = javaObject;
    }

    /**
     * Returns the owner object. An owner object is the one opposite to a referenced object.
     */
    public BoundObject getOwner() {
        return owner;
    }

    /**
     * Returns the Java object associated with this object.
     */
    public Object getJavaObject() {
        return javaObject;
    }

    /**
     * Returns the object referenced by the given Java field name.
     */
    public BoundObject getReferencedObject(String fieldName) {
        BoundObject boundObject = references.get(fieldName);
        return boundObject;
    }

    /**
     * Initializes the owner object.
     */
    void setOwner(BoundObject owner) {
        this.owner = owner;
    }

    /**
     * Adds a referenced object.
     */
    void addReference(String fieldName, BoundObject bObj) {
        if (references == null) {
            references = new HashMap<>();
        }
        references.put(fieldName, bObj);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + javaObject;
    }
}
