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
package com.karmant.syn.sample.script.rt.value;

/**
 * Type of a value.
 */
public enum ValueType {
    VOID("void"),
    NULL("null"),
    BOOLEAN("boolean"),
    INT("int"),
    LONG("long"),
    DOUBLE("double"),
    STRING("String"),
    ARRAY("array"),
    ARRAY_ELEMENT("array_element"),
    VARIABLE("variable"),
    BLOCK("block"),
    FUNCTION("function"),
    CLASS("class"),
    OBJECT("object"),
    JAVACLASS("javaclass"),
    JAVAOBJECT("javaobject"),
    JAVAFIELD("javafield"),
    JAVAMETHOD("javamethod");
    
    private final String typeName;

    private ValueType(String descriptiveName) {
        this.typeName = descriptiveName;
    }
    
    /**
     * Returns a human-readable name of this type. Used in exception messages.
     */
    public String getTypeName() {
        return typeName;
    }
}
