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
package com.karmant.syn;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * Reflection utilities used by the Binder.
 */
final class BinderReflectionUtil {
    private BinderReflectionUtil(){}
    
    /**
     * Returns the collection of {@link SynInit} methods defined in the given class. Inherited
     * methods are not included.
     */
    static Collection<InitMethod> getInitMethodsForClass(Class<?> cls) throws SynBinderException {
        Collection<InitMethod> initMethods = new ArrayList<>();
        
        for (Method method : cls.getDeclaredMethods()) {
            SynInit synInit = method.getAnnotation(SynInit.class);
            if (synInit != null) {
                InitMethod initMethod = createInitMethod(method);
                initMethods.add(initMethod);
            }
        }
        
        return initMethods;
    }

    /**
     * Creates an {@link InitMethod} instance for the given Java method.
     */
    private static InitMethod createInitMethod(Method method) throws SynBinderException {
        int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers)) {
            throw new SynBinderException(String.format(
                    "Method %s is abstract, it cannot be used as an init method",
                    method));
        }
        if (Modifier.isStatic(modifiers)) {
            throw new SynBinderException(String.format(
                    "Method %s is static, it cannot be used as an init method",
                    method));
        }
        if (Modifier.isNative(modifiers)) {
            //Maybe native methods could be supported, but here they are not allowed for reliability.
            throw new SynBinderException(String.format(
                    "Method %s is native, it cannot be used as an init method",
                    method));
        }
        if (method.getParameterTypes().length != 0) {
            throw new SynBinderException(String.format(
                    "Method %s has parameters, it cannot be used as an init method",
                    method));
        }
        InitMethod initMethod = new InitMethod(method);
        return initMethod;
    }
    
    /**
     * Returns the Java class with the given name. Throws an exception if the class does not comply
     * Binder limitations.
     */
    static Class<?> getPrClassByName(ClassLoader classLoader, String clsName) throws SynBinderException {
        Class<?> c = getClassByName(classLoader, clsName);
        Constructor<?> constructor = getClassConstructor(clsName, c);
        checkClassAndConstructorProperties(clsName, c, constructor);
        return c;
    }

    /**
     * Finds a Java class by its fully qualified name. Wraps an exception.
     */
    private static Class<?> getClassByName(ClassLoader classLoader, String clsName)
            throws SynBinderException
    {
        try {
            Class<?> c = Class.forName(clsName, true, classLoader);
            return c;
        } catch (ClassNotFoundException e) {
            String msg = String.format("Unable to find class by name: %s", clsName);
            throw new SynBinderException(msg, e);
        }
    }

    /**
     * Returns Java class' default constructor. Wraps an exception.
     */
    private static Constructor<?> getClassConstructor(String clsName, Class<?> c)
            throws SynBinderException
    {
        try {
            Constructor<?> constructor = c.getConstructor();
            return constructor;
        } catch (NoSuchMethodException e) {
            String msg = String.format("Class %s does not have a default constructor", clsName);
            throw new SynBinderException(msg, e);
        }
    }

    /**
     * Checks whether a Java constructor satisfies Binder limitations.
     */
    private static void checkClassAndConstructorProperties(
            String clsName,
            Class<?> c,
            Constructor<?> constructor) throws SynBinderException
    {
        if (!Modifier.isPublic(constructor.getModifiers())) {
            String msg = String.format("Class %s does not have a default public constructor", clsName);
            throw new SynBinderException(msg);
        }
        
        int modifiers = c.getModifiers();
        if (Modifier.isAbstract(modifiers)) {
            throw new SynBinderException(String.format(
                    "Class %s is abstract", c.getCanonicalName()));
        }
        if (!Modifier.isPublic(modifiers)) {
            throw new SynBinderException(String.format(
                    "Class %s is not public", c.getCanonicalName()));
        }
        if (c.isAnnotation()) {
            throw new SynBinderException(String.format(
                    "Class %s is an annotation", c.getCanonicalName()));
        }
        if (c.isEnum()) {
            throw new SynBinderException(String.format(
                    "Class %s is an enum", c.getCanonicalName()));
        }
        if (c.isInterface()) {
            throw new SynBinderException(String.format(
                    "Class %s is an interface", c.getCanonicalName()));
        }
    }

    /**
     * Throws an exception if there is a Java field in the given class, which is marked with {@link SynField}
     * annotation, but not included in the given set. Inherited fields are verified as well.
     */
    static void ensureAllFieldsAreDefined(Class<?> cls, Set<String> definedFields) throws SynBinderException {
        Class<?> curCls = cls;
        while (curCls != null) {
            for (Field field : curCls.getDeclaredFields()) {
                String fieldName = field.getName();
                if (field.getAnnotation(SynField.class) != null && !definedFields.contains(fieldName)) {
                    throw new SynBinderException(String.format(
                            "Field '%s' of class %s is not defined in the grammar",
                            fieldName, cls.getCanonicalName()));
                }
            }
            curCls = curCls.getSuperclass();
        }
    }
    
    /**
     * Sets a Java field value. Handles private fields. Wraps exceptions.
     */
    static void setFieldValue(Field field, Object obj, Object value) throws SynBinderException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                throw new SynBinderException(String.format("Unable to set field %s", field), e);
            }
        } finally {
            field.setAccessible(accessible);
        }
    }

    /**
     * Gets a Java field value. Handles private fields. Wraps exceptions.
     */
    static Object getFieldValue(Field field, Object obj) throws SynBinderException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            try {
                return field.get(obj);
            } catch (IllegalAccessException e) {
                throw new SynBinderException(String.format(
                        "Unable to get value by field %s", field), e);
            }
        } finally {
            field.setAccessible(accessible);
        }
    }
}
