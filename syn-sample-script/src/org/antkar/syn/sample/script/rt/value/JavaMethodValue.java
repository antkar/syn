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

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.javacls.JavaMethodSet;

/**
 * Java method value. Allows to invoke Java methods from a script.
 */
abstract class JavaMethodValue extends Value {
    private final JavaMethodSet methods;

    JavaMethodValue(JavaMethodSet methods) {
        this.methods = methods;
    }
    
    /**
     * Returns the set of Java methods.
     */
    final JavaMethodSet getMethods() {
        return methods;
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.JAVAMETHOD;
    }
    
    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(getMethods() + "");
    }

    @Override
    public abstract Value call(RValue[] arguments) throws SynsException;
}
