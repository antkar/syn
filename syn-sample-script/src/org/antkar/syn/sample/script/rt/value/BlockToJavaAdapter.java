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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.ThrowSynsException;
import org.antkar.syn.sample.script.rt.javacls.TypeMatchPrecision;

/**
 * Provides a possibility to use a Script Language block construction as a Java interface
 * implementation.
 */
final class BlockToJavaAdapter {
    private BlockToJavaAdapter(){}

    /**
     * Returns an object that implements the specified interface and redirects method calls
     * to the specified Script Language block.
     */
    static Object createAdapter(BlockValue blockValue, Class<?> javaInterface) throws SynsException {
        InvocationHandler handler = createInvocationHandler(blockValue, javaInterface);
        ClassLoader classLoader = javaInterface.getClassLoader();
        Class<?>[] interfaces = new Class<?>[]{javaInterface};
        return Proxy.newProxyInstance(classLoader, interfaces, handler);
    }
    
    /**
     * Creates an invocation handler for redirecting interface method calls to a Script Language
     * block.
     */
    private static InvocationHandler createInvocationHandler(
            BlockValue blockValue,
            Class<?> javaInterface) throws SynsException
    {
        Map<String, Method> methods = getInterfaceMethods(javaInterface);
        
        if (methods.isEmpty()) {
            //No methods - return a special null invocation handler.
            return new NoMethodsInvocationHandler(blockValue);
        } else if (methods.size() == 1) {
            String name = methods.keySet().iterator().next();
            if (!blockValue.hasFunction(name)) {
                //The interface defines a single method, and there is no function with the same
                //name defined in the block. So the method will be redirected to the block
                //itself.
                return new SingleMethodInvocationHandler(blockValue);
            }
        }
        
        //Methods of the interface have to be redirected to corresponding functions defined
        //in the block.
        return new MultipleMehtodsInvocationHandler(blockValue);
    }
    
    /**
     * Returns the map of interface methods.
     */
    private static Map<String, Method> getInterfaceMethods(Class<?> javaInterface) throws SynsException {
        Map<String, Method> map = new HashMap<>();
        
        for (Method method : javaInterface.getMethods()) {
            if (method.isVarArgs()) {
                throw SynsException.format(
                        "Cannot implement method %s: it has a variable number of arguments",
                        method);
            }
            
            int modifiers = method.getModifiers();
            if (Modifier.isAbstract(modifiers)) {
                //In Java 8, an interface can contain non-abstract methods: static methods and
                //default methods. For simplicity such methods are ignored, otherwise, for instance,
                //it is impossible to implement java.util.Comparator, because it has overloaded
                //static and default methods. (Implementing an interface which has overloaded methods
                //is not supported for simplicity reasons, too.)
                putMethodToMap(javaInterface, method, map);
            }
        }
        
        return map;
    }

    /**
     * Adds a method into a map, performing additional checks.
     */
    private static void putMethodToMap(Class<?> javaInterface, Method method, Map<String, Method> map)
            throws SynsException
    {
        String name = method.getName();
        Method otherMethod = map.put(name, method);
        if (otherMethod != null) {
            //Implementing overloaded methods is not possible, since the Script Language does not
            //allow to define two functions with the same name, even if they have different
            //formal parameters.
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?>[] otherParameterTypes = otherMethod.getParameterTypes();
            if (!Arrays.equals(parameterTypes, otherParameterTypes)) {
                throw SynsException.format(
                        "Cannot implement Java interface %s: it has overloaded method %s",
                        javaInterface.getCanonicalName(),
                        name);
            }
        }
    }
    
    /**
     * Map of default return values for primitive types.
     */
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUES;
    
    static {
        Map<Class<?>, Object> map = new HashMap<>();
        map.put(byte.class, Byte.valueOf((byte)0));
        map.put(short.class, Short.valueOf((short)0));
        map.put(int.class, Integer.valueOf(0));
        map.put(long.class, Long.valueOf(0));
        map.put(float.class, Float.valueOf(0));
        map.put(double.class, Double.valueOf(0));
        map.put(char.class, Character.valueOf((char)0));
        map.put(boolean.class, Boolean.FALSE);
        PRIMITIVE_DEFAULT_VALUES = Collections.unmodifiableMap(map);
    }
    
    /**
     * Returns the default return value for the specified method return type.
     */
    private static Object getDefaultValue(Class<?> type) {
        if (!type.isPrimitive() || void.class.equals(type)) {
            return null;
        } else {
            return PRIMITIVE_DEFAULT_VALUES.get(type);
        }
    }
    
    /**
     * Converts a {@link Value} returned by a Script Language block to a Java method return value.
     */
    private static Object getJavaReturnValue(Method method, Value value) throws SynsException {
        Class<?> returnType = method.getReturnType();
        
        //Cannot return a non-void value from a void method.
        if (void.class.equals(returnType)) {
            if (!value.isVoid()) {
                throw SynsException.format(
                        "Method %s is void, but %s was returned by script",
                        method,
                        value.getTypeMessage());
            }
            return null;
        }
        
        //Convert the value to Java.
        RValue rvalue = value.toRValue();
        Object jvalue = rvalue.toJava(returnType, TypeMatchPrecision.NULL);
        if (jvalue == RValue.INVALID) {
            throw SynsException.format(
                    "Invalid return value %s for method %s", rvalue.getTypeMessage(), method);
        }
        
        return jvalue;
    }
    
    /**
     * Block-based proxy invocation handler.
     */
    private static abstract class ScriptInvocationHandler implements InvocationHandler {
        final BlockValue blockValue;
        
        ScriptInvocationHandler(BlockValue blockValue) {
            this.blockValue = blockValue;
        }

        @Override
        public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(method.getDeclaringClass())) {
                //This is the one of java.lang.Object's methods. Redirect it to the
                //block Java object.
                return invokeObjectMethod(method, args);
            }
            
            return invoke0(proxy, method, args);
        }

        /**
         * Invokes a Java method on the block Java object.
         */
        private Object invokeObjectMethod(Method method, Object[] args)
                throws IllegalAccessException, InvocationTargetException
        {
            if ("equals".equals(method.getName())) {
                //Equals has to be handled in a special way to work properly for the same
                //block value wrapped in different proxies.
                Object arg = args[0];
                if (Proxy.isProxyClass(arg.getClass())) {
                    InvocationHandler handler = Proxy.getInvocationHandler(arg);
                    if (handler instanceof ScriptInvocationHandler) {
                        args[0] = ((ScriptInvocationHandler)handler).blockValue;
                    }
                }
            }
            return method.invoke(blockValue, args);
        }
        
        /**
         * Redirects the specified Java method to the block.
         */
        abstract Object invoke0(Object proxy, Method method, Object[] args) throws Throwable;
    }
    
    /**
     * Invocation handler for an interface which defines no methods.
     */
    private static class NoMethodsInvocationHandler extends ScriptInvocationHandler {
        NoMethodsInvocationHandler(BlockValue blockValue) {
            super(blockValue);
        }

        @Override
        Object invoke0(Object proxy, Method method, Object[] args) throws Throwable {
            //Must not be called, since there are no methods in the interface.
            throw new IllegalStateException();
        }
    }
    
    /**
     * Invocation handler that redirects a interface method calls to the body of a Script Language
     * block.
     */
    private static class SingleMethodInvocationHandler extends ScriptInvocationHandler {
        SingleMethodInvocationHandler(BlockValue blockValue) {
            super(blockValue);
        }

        @Override
        Object invoke0(Object proxy, Method method, Object[] args) throws Throwable {
            Value value;
            try {
                value = blockValue.call(RValue.ARRAY0);
            } catch (ThrowSynsException e) {
                throw e.getCause();
            }
            return getJavaReturnValue(method, value);
        }
    }
    
    /**
     * Invocation handler that redirects a interface method calls to functions defined in
     * a Script Language block.
     */
    private static class MultipleMehtodsInvocationHandler extends ScriptInvocationHandler {
        MultipleMehtodsInvocationHandler(BlockValue blockValue) {
            super(blockValue);
        }

        @Override
        Object invoke0(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (!blockValue.hasFunction(name)) {
                //If there is no such function in the block, return the default value instead of
                //throwing an exception. This feature allows to implement only those interface
                //methods that are really important. E. g. there is no need to implement all 5
                //methods defined in java.awt.event.MouseListener if only mouse click event
                //has to be handled.
                return getDefaultValue(method.getReturnType());
            }
            
            //Convert Java argument values to Script Language values.
            RValue[] arguments = javaArgumentsToScriptArguments(args);
            
            //Call the function.
            Value value;
            try {
                value = blockValue.callFunction(name, arguments);
            } catch (ThrowSynsException e) {
                throw e.getCause();
            }
            
            //Convert the value returned by the function to Java.
            return getJavaReturnValue(method, value);
        }

        /**
         * Converts Java argument values to Script Language values.
         */
        private RValue[] javaArgumentsToScriptArguments(Object[] args) throws SynsException {
            if (args == null || args.length == 0) {
                return RValue.ARRAY0;
            }
            
            RValue[] arguments = new RValue[args.length];
            for (int i = 0; i < args.length; ++i) {
                RValue value = Value.forJavaObject(args[i]);
                arguments[i] = value;
            }
            
            return arguments;
        }
    }
}
