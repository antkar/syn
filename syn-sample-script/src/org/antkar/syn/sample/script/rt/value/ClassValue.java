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
package org.antkar.syn.sample.script.rt.value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.schema.Declaration;
import org.antkar.syn.sample.script.schema.FunctionDeclaration;

/**
 * Script class value. Associated with names of classes defined in a script.
 */
class ClassValue extends Value {
    /** The name of the class. */
    private final String className;
    
    /** The scope of class body. Class constants are defined in that scope. */
    private final ScriptScope classScope;
    
    /** Constructor function. Can be <code>null</code>. */
    private final FunctionDeclaration constructor;
    
    /** Static (constant) members. */
    private final Map<String, RValue> staticMembers;
    
    /** Instance member declarations (variables and functions). */
    private final List<Declaration> instanceMembers;

    ClassValue(
            String className,
            ScriptScope classScope,
            FunctionDeclaration constructor,
            Map<String, RValue> staticMembers,
            List<Declaration> instanceMembers)
    {
        this.className = className;
        this.classScope = classScope;
        this.constructor = constructor;
        this.staticMembers = staticMembers;
        this.instanceMembers = instanceMembers;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.CLASS;
    }
    
    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(className);
    }

    /**
     * Returns the name of the class.
     */
    String getClassName() {
        return className;
    }
    
    @Override
    public Value getMemberOpt(String name) {
        return staticMembers.get(name);
    }
    
    @Override
    public Value newObject(RValue[] arguments) throws SynsException {
        //Create the scope of the object. Instance variables and functions will be defined in that scope,
        //and all instance functions will be executed in that scope.
        ScriptScope objectScope = classScope.deriveClassScope("object " + className, true);
        
        //Add instance members into the object's scope, get their initial values.
        Map<String, Value> memberValues = new HashMap<>();
        for (Declaration declaration : instanceMembers) {
            Value value = declaration.addToScope(objectScope);
            memberValues.put(declaration.getName(), value);
        }
        
        //Call the constructor, if any.
        if (constructor != null) {
            constructor.getFunction().call(objectScope, arguments);
        }
        
        //Create a new object value.
        return new ObjectValue(this, memberValues);
    }
}
