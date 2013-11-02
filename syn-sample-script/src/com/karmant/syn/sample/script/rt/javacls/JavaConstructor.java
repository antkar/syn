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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Wraps a Java {@link Constructor}.
 */
class JavaConstructor extends JavaAbstractMethod {
	private final Constructor<?> constructor;
	
	JavaConstructor(Constructor<?> constructor) {
		this.constructor = constructor;
	}

	@Override
	boolean isVarArgs() {
		return constructor.isVarArgs();
	}

	@Override
	boolean isStatic() {
		return true;
	}
	
	@Override
	boolean isVoid() {
		return false;
	}

	@Override
	Class<?>[] getParameterTypes() {
		return constructor.getParameterTypes();
	}

	@Override
	Object invokeJava(Object obj, Object[] arguments)
			throws InvocationTargetException, InstantiationException, IllegalAccessException
	{
		return constructor.newInstance(arguments);
	}
	
	@Override
	public String toString() {
		return constructor + "";
	}
}
