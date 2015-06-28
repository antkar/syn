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

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.schema.ClassDeclaration;
import org.antkar.syn.sample.script.schema.Declaration;
import org.antkar.syn.sample.script.schema.FunctionDeclaration;

/**
 * Script class value. Associated with names of classes defined in a script.
 */
final class ClassValue extends Value {
    /** The declaration of this class. */
    private final ClassDeclaration classDeclaration;
    
    /** The scope of class body. Class constants are defined in that scope. */
    private final ScriptScope classScope;
    
    private final RValue[] staticMemberValues;
    
    private final String objectScopeDescription;

    ClassValue(
            ClassDeclaration classDeclaration,
            ScriptScope classScope,
            RValue[] staticMemberValues)
    {
        this.classDeclaration = classDeclaration;
        this.classScope = classScope;
        this.staticMemberValues = staticMemberValues;
        
        objectScopeDescription = "object " + classDeclaration.getName();
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.CLASS;
    }
    
    @Override
    public String getTypeMessage() {
        String className = classDeclaration.getName();
        return getCompoundTypeMessage(className);
    }

    ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }
    
    @Override
    public Value call(RValue[] arguments) throws SynsException {
        return newObject(arguments);
    }
    
    @Override
    public Value getMemberOpt(String name, ScriptScope readerScope) {
        ClassMemberDescriptor descriptor = classDeclaration.getStaticMemberDescriptorOpt(name);
        return descriptor == null ? null : descriptor.read(this, null, readerScope);
    }
    
    @Override
    public Value newObject(RValue[] arguments) throws SynsException {
        //Create a new object value.
        Value[] memberValues = new Value[classDeclaration.getInstanceMemberDescriptors().size()];
        ObjectValue objectValue = new ObjectValue(this, memberValues);

        //Create the scope of the object. Instance variables and functions will be defined in that scope,
        //and all instance functions will be executed in that scope.
        ScriptScope objectScope = classScope.nestedObjectScope(objectScopeDescription, objectValue);
        
        //Add instance members into the object's scope, get their initial values.
        initializeInstanceMembers(objectScope, memberValues, true);
        initializeInstanceMembers(objectScope, memberValues, false);
        
        //Call the constructor, if any.
        FunctionDeclaration constructor = classDeclaration.getConstructor();
        if (constructor != null) {
            constructor.getFunction().call(objectScope, arguments);
        }
        
        return objectValue;
    }
    
    private void initializeInstanceMembers(
            ScriptScope objectScope,
            Value[] memberValues,
            boolean function) throws SynsException
    {
        for (ClassMemberDescriptor descriptor : classDeclaration.getInstanceMemberDescriptors()) {
            Declaration declaration = descriptor.getDeclaration();
            if (declaration.isFunction() == function) {
                Value value = declaration.addToScope(objectScope);
                memberValues[descriptor.getIndex()] = value;
            }
        }
    }
    
    @Override
    ValueType getTypeofValueType() {
        return getValueType();
    }
    
    Value readValue(int index) {
        return staticMemberValues[index];
    }
}
