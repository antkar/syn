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
package com.karmant.syn.sample.script.rt.javacls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Wraps a Java {@link Method}.
 */
class JavaMethod extends JavaAbstractMethod {
	private final Method method;
	
	JavaMethod(Method method) {
		this.method = method;
	}
	
	@Override
	boolean isVarArgs() {
		return method.isVarArgs();
	}
	
	@Override
	boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}
	
	@Override
	boolean isVoid() {
		return void.class.equals(method.getReturnType());
	}
	
	@Override
	Class<?>[] getParameterTypes() {
		return method.getParameterTypes();
	}

	@Override
	Object invokeJava(Object obj, Object[] arguments)
			throws IllegalAccessException, InvocationTargetException
	{
		return method.invoke(obj, arguments);
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
