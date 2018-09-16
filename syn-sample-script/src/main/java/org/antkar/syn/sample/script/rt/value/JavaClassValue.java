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
import org.antkar.syn.sample.script.rt.javacls.JavaClass;

/**
 * Java class value. Allows to create instances of a Java class and access its static members.
 */
final class JavaClassValue extends Value {
    private final JavaClass cls;

    JavaClassValue(JavaClass cls) {
        this.cls = cls;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.JAVACLASS;
    }

    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(cls.getJavaClass().getCanonicalName());
    }

    @Override
    public Value getMemberOpt(String name, ScriptScope readerScope) throws SynsException {
        return cls.getStaticMemberOpt(name);
    }

    @Override
    public Value call(RValue[] arguments) throws SynsException {
        return newObject(arguments);
    }

    @Override
    public Value newObject(RValue[] arguments) throws SynsException {
        return cls.newInstance(arguments);
    }

    @Override
    ValueType getTypeofValueType() throws SynsException {
        return getValueType();
    }
}
