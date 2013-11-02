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
import java.lang.reflect.Method;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.RValue;

/**
 * Common superclass for Java constructor and method wrappers. Used to provide an unified
 * interface to both {@link Constructor} and {@link Method} classes, along with some
 * additional common functionality.
 */
abstract class JavaAbstractMethod {
	JavaAbstractMethod(){}
	
	/**
	 * Returns <code>true</code> if the method/constructor is variadic.
	 */
	abstract boolean isVarArgs();
	
	/**
	 * Returns <code>true</code> if the method is static. Always <code>true</code> for a constructor.
	 */
	abstract boolean isStatic();
	
	/**
	 * Returns <code>true</code> if the return type of the method is <code>void</code>.
	 */
	abstract boolean isVoid();
	
	/**
	 * Returns the method's/constructor's parameter types.
	 */
	abstract Class<?>[] getParameterTypes();
	
	/**
	 * Invokes the method/constructor directly.
	 */
	abstract Object invokeJava(Object obj, Object[] arguments)
			throws IllegalAccessException, InvocationTargetException, InstantiationException;
	
	/**
	 * Invokes the method/constructor. Wraps Java reflection exceptions.
	 * Converts arguments from {@link RValue}s to Java {@link Object}s.
	 * Handles variable number of arguments appropriately.
	 */
	final Object invoke(Object obj, RValue[] rArguments) throws SynsException {
		//Convert fixed arguments.
		Class<?>[] parameterTypes = getParameterTypes();
		Object[] arguments = new Object[parameterTypes.length];
		convertFixedArguments(rArguments, parameterTypes, arguments);
		
		//Convert variable arguments.
		if (isVarArgs()) {
			convertVariableArguments(rArguments, parameterTypes, arguments);
		}
		
		//Invoke Java method/constructor.
		try {
			try {
				Object result = invokeJava(obj, arguments);
				return result;
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		} catch (SynsException e) {
			throw e;
		} catch (IllegalAccessException | InstantiationException e) {
			throw new SynsException(e);
		} catch (Throwable e) {
			throw new SynsException(e);
		}
	}
	
	/**
	 * Converts fixed method/constructor arguments from {@link RValue}s to Java {@link Object}s.
	 */
	private void convertFixedArguments(
			RValue[] rArguments,
			Class<?>[] parameterTypes,
			Object[] arguments) throws SynsException
	{
		int length = arguments.length - (isVarArgs() ? 1 : 0);
		for (int i = 0; i < length; ++i) {
			RValue rArgument = rArguments[i];
			Class<?> type = parameterTypes[i];
			Object argument = convertArgument(rArgument, type);
			arguments[i] = argument;
		}
	}

	/**
	 * Converts variable method/constructor arguments from {@link RValue}s to Java {@link Object}s.
	 */
	private void convertVariableArguments(
			RValue[] rArguments,
			Class<?>[] parameterTypes,
			Object[] arguments) throws SynsException
	{
		int varArgsOfs = parameterTypes.length - 1;
		Object[] varArgs = new Object[rArguments.length - varArgsOfs];
		Class<?> type = parameterTypes[varArgsOfs].getComponentType();
		for (int i = 0; i < varArgs.length; ++i) {
			RValue rArgument = rArguments[varArgsOfs + i];
			Object argument = convertArgument(rArgument, type);
			varArgs[i] = argument;
		}
		arguments[varArgsOfs] = varArgs;
	}

	/**
	 * Converts a method/constructor argument from {@link RValue} to Java {@link Object}.
	 */
	private Object convertArgument(RValue rArgument, Class<?> type) throws SynsException {
		Object argument = rArgument.toJava(type, TypeMatchPrecision.NULL);
		if (argument == RValue.INVALID) {
			//Must not get here.
			throw new IllegalStateException();
		}
		return argument;
	}

	/**
	 * Checks whether the specified arguments are acceptable for this method.
	 * If arguments are acceptable, the precision is returned. Otherwise, <code>-1</code> is returned.
	 */
	int matchArguments(RValue[] arguments) throws SynsException {
		Class<?>[] types = getParameterTypes();
		if (!isVarArgs()) {
			//Fixed number of arguments.
			if (types.length != arguments.length) {
				return -1;
			}
			return matchTypes(types, arguments, types.length);
		}
		
		//Variable number of arguments.
		if (arguments.length < types.length - 1) {
			return -1;
		}
		if (matchTypes(types, arguments, types.length - 1) == -1) {
			return -1;
		}
		
		return matchVarArguments(arguments, types);
	}

	/**
	 * Checks types of variable arguments.
	 */
	private int matchVarArguments(RValue[] arguments, Class<?>[] types) throws SynsException {
		Class<?> varArrayType = types[types.length - 1];
		Class<?> varType = varArrayType.getComponentType();
		
		DefaultTypeMatchPrecision precision = new DefaultTypeMatchPrecision();
		
		for (int i = types.length - 1; i < arguments.length; ++i) {
			Object object = arguments[i].toJava(varType, precision);
			if (object == RValue.INVALID) {
				return -1;
			}
		}
		
		return precision.getPrecision();
	}
	
	/**
	 * Checks types of the specified arguments.
	 */
	private static int matchTypes(Class<?>[] types, RValue[] arguments, int end) throws SynsException {
		DefaultTypeMatchPrecision precision = new DefaultTypeMatchPrecision();
		
		for (int i = 0; i < end; ++i) {
			Object object = arguments[i].toJava(types[i], precision);
			if (object == RValue.INVALID) {
				return -1;
			}
		}
		
		return precision.getPrecision();
	}
}
