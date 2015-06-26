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

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.javacls.JavaMethodSet;

/**
 * Instance (not static) Java method value.
 */
public class JavaInstanceMethodValue extends JavaMethodValue {
    private final Object obj;

    public JavaInstanceMethodValue(JavaMethodSet methods, Object obj) {
        super(methods);
        this.obj = obj;
    }

    @Override
    public Value call(RValue[] arguments) throws SynsException {
        return getMethods().callInstance(obj, arguments);
    }
}
