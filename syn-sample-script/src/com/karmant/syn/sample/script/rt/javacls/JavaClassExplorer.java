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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Explores Java class structure.
 */
final class JavaClassExplorer {
	private JavaClassExplorer(){}
	
	/**
	 * Returns a method set containing constructors of the specified Java class.
	 */
	static JavaMethodSet discoverConstructors(Class<?> cls) {
		List<JavaConstructor> javaConstructors = new ArrayList<>();
		
		for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
			if (Modifier.isPublic(constructor.getModifiers())) {
				javaConstructors.add(new JavaConstructor(constructor));
			}
		}
		
		return new JavaMethodSet(cls.getCanonicalName(), javaConstructors);
	}

	/**
	 * Returns a map of fields, methods and inner classes of the specified Java class.
	 */
	static Map<String, JavaMember> discoverFieldsAndMethods(Class<?> cls) {
		Map<String, JavaClass> classesMap = new HashMap<>();
		discoverClasses(cls, classesMap);

		Map<String, JavaField> fieldsMap = new HashMap<>();
		discoverFields(cls, fieldsMap);

		Map<String, JavaMethodSet> methodsMap = new HashMap<>();
		discoverMethods(cls, methodsMap);
		
		return createMembersMap(classesMap, fieldsMap, methodsMap);
	}

	/**
	 * Creates a combined map of Java members.
	 */
	private static Map<String, JavaMember> createMembersMap(
			Map<String, JavaClass> classesMap,
			Map<String, JavaField> fieldsMap,
			Map<String, JavaMethodSet> methodsMap)
	{
		Set<String> names = new HashSet<>(fieldsMap.keySet());
		names.addAll(methodsMap.keySet());
		names.addAll(classesMap.keySet());

		Map<String, JavaMember> map = new HashMap<>();
		for (String name : names) {
			JavaClass javaClass = classesMap.get(name);
			JavaField javaField = fieldsMap.get(name);
			JavaMethodSet javaMethods = methodsMap.get(name);
			JavaMember member = createMember(name, javaClass, javaField, javaMethods);
			map.put(name, member);
		}
		
		return map;
	}

	/**
	 * Discovers inner classes of the specified Java class.
	 */
	private static void discoverClasses(Class<?> cls, Map<String, JavaClass> wrappedClasses) {
		Map<String, Class<?>> classes = discoverInnerClassesMap(cls);
		for (String name : classes.keySet()) {
			Class<?> innerClass = classes.get(name);
			wrappedClasses.put(name, JavaClass.getInstance(innerClass));
		}
	}

	/**
	 * Returns the map of inner classes of the specified Java class.
	 */
	private static Map<String, Class<?>> discoverInnerClassesMap(Class<?> cls) {
		Map<String, Class<?>> classes = new HashMap<>();
		
		for (Class<?> innerClass : cls.getClasses()) {
			if (Modifier.isStatic(innerClass.getModifiers())) {
				String name = innerClass.getSimpleName();
				Class<?> oldInnerClass = classes.get(name);
				if (oldInnerClass == null || isInnerClassHiding(innerClass, oldInnerClass)) {
					classes.put(name, innerClass);
				}
			}
		}
		
		return classes;
	}

	/**
	 * Discovers Java class fields.
	 */
	private static void discoverFields(Class<?> cls, Map<String, JavaField> wrappedFields) {
		Map<String, Field> fields = discoverFieldsMap(cls);
		for (String name : fields.keySet()) {
			Field field = fields.get(name);
			wrappedFields.put(name, new JavaField(field));
		}
	}

	/**
	 * Returns the map of Java fields of the specified Java class.
	 */
	private static Map<String, Field> discoverFieldsMap(Class<?> cls) {
		Map<String, Field> fields = new HashMap<>();
		
		for (Field field : cls.getFields()) {
			if (acceptJavaField(field)) {
				String name = field.getName();
				Field oldField = fields.get(name);
				if (oldField == null || isFieldHiding(field, oldField)) {
					fields.put(name, field);
				}
			}
		}
		
		return fields;
	}

	/**
	 * Discovers methods of the specified Java class.
	 */
	private static void discoverMethods(Class<?> cls, Map<String, JavaMethodSet> wrappedMethods) {
		Map<MethodSignature, Method> methods = discoverMethodsMap(cls);
		Map<String, List<JavaMethod>> methodsByName = groupOverloadedMethods(methods);
		
		for (String name : methodsByName.keySet()) {
			List<JavaMethod> list = methodsByName.get(name);
			wrappedMethods.put(name, new JavaMethodSet(name, list));
		}
	}

	/**
	 * Returns the map of Java methods grouped by name.
	 */
	private static Map<String, List<JavaMethod>> groupOverloadedMethods(
			Map<MethodSignature, Method> methods)
	{
		Map<String, List<JavaMethod>> methodsByName = new HashMap<>();
		
		for (MethodSignature sign : methods.keySet()) {
			Method method = methods.get(sign);
			
			List<JavaMethod> list = methodsByName.get(sign.name);
			if (list == null) {
				list = new ArrayList<>();
				methodsByName.put(sign.name, list);
			}
			list.add(new JavaMethod(method));
		}
		
		return methodsByName;
	}

	/**
	 * Returns the map of Java methods for the specified Java class.
	 */
	private static Map<MethodSignature, Method> discoverMethodsMap(Class<?> cls) {
		Map<MethodSignature, Method> methods = new HashMap<>();
		
		//Discover interface methods. This is necessary for the case when a non-public class
		//implements an interface, so the method defined in the class cannot be called on an
		//object and the corresponding method defined in the interface has to be used instead.
		Class<?> curCls = cls;
		while (curCls != null) {
			discoverMethodsForClass(curCls, methods);
			for (Class<?> intf : curCls.getInterfaces()) {
				discoverMethodsForClass(intf, methods);
			}
			curCls = curCls.getSuperclass();
		}
		
		return methods;
	}

	/**
	 * Discovers Java class methods.
	 */
	private static void discoverMethodsForClass(Class<?> cls, Map<MethodSignature, Method> methods) {
		for (Method method : cls.getMethods()) {
			if (acceptJavaMethod(method)) {
				String name = method.getName();
				MethodSignature sign = new MethodSignature(name, method.getParameterTypes());
				Method oldMethod = methods.get(sign);
				if (oldMethod == null || isMethodOverriding(method, oldMethod)) {
					methods.put(sign, method);
				}
			}
		}
	}
	
	/**
	 * Checks whether one inner class hides another one.
	 */
	private static boolean isInnerClassHiding(Class<?> subinnerClass, Class<?> superinnerClass) {
		Class<?> subclass = subinnerClass.getDeclaringClass();
		Class<?> superclass = superinnerClass.getDeclaringClass();
		return isClassMoreSpecific(subclass, superclass);
	}
	
	/**
	 * Checks whether one field hides another one.
	 */
	private static boolean isFieldHiding(Field subfield, Field superfield) {
		Class<?> subclass = subfield.getDeclaringClass();
		Class<?> superclass = superfield.getDeclaringClass();
		return isClassMoreSpecific(subclass, superclass);
	}
	
	/**
	 * Checks whether one method hides another one.
	 */
	private static boolean isMethodOverriding(Method submethod, Method supermethod) {
		Class<?> subclass = submethod.getDeclaringClass();
		Class<?> superclass = supermethod.getDeclaringClass();
		return isClassMoreSpecific(subclass, superclass);
	}
	
	/**
	 * Checks whether one class is more specific than another one.
	 */
	private static boolean isClassMoreSpecific(Class<?> subclass, Class<?> superclass) {
		return !subclass.equals(superclass) && superclass.isAssignableFrom(subclass);
	}
	
	/**
	 * Returns <code>true</code> if the specified Java field has to be taken into account.
	 */
	private static boolean acceptJavaField(Field field) {
		//Skip fields declared in non-public classes.
		int classModifiers = field.getDeclaringClass().getModifiers();
		int modifiers = field.getModifiers();
		return Modifier.isPublic(classModifiers) && Modifier.isPublic(modifiers);
	}
	
	/**
	 * Returns <code>true</code> if the specified Java method has to be taken into account.
	 */
	private static boolean acceptJavaMethod(Method method) {
		//Skip methods declared in non-public classes, like a method in an inner private class
		//that implements an interface - the method declared in the interface has to be used instead
		//(since it is not possible to call a method declared in non-public class through reflection
		//API, without calling setAccessible(true)).
		int classModifiers = method.getDeclaringClass().getModifiers();
		int modifiers = method.getModifiers();
		return Modifier.isPublic(classModifiers) && Modifier.isPublic(modifiers);
	}

	/**
	 * Creates a Java member instance.
	 */
	private static JavaMember createMember(
			String name,
			JavaClass javaClass,
			JavaField javaField,
			JavaMethodSet javaMethods)
	{
		//For simplicity, if there is a conflict, classes hide fields and methods, and methods hide
		//fields. It is possible to make all conflicting members accessible simultaneously, but such
		//cases seem to be rare, and supporting them is not worth the efforts (at the moment).
		
		if (javaClass != null) {
			return new JavaInnerClass(javaClass);
		} else if (javaMethods != null) {
			return javaMethods;
		} else {
			assert javaField != null;
			return javaField;
		}
	}
	
	/**
	 * Java method signature. Contains method's name and parameter types.
	 */
	private static final class MethodSignature {
		private final String name;
		private final Class<?>[] params;
		private final int hashCode;
		
		MethodSignature(String name, Class<?>[] params) {
			this.name = name;
			this.params = params.clone();
			hashCode = name.hashCode() * 31 + Arrays.hashCode(params);
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MethodSignature) {
				MethodSignature sign = (MethodSignature)obj;
				if (hashCode != sign.hashCode) {
					return false;
				}
				if (!name.equals(sign.name)) {
					return false;
				}
				return Arrays.equals(params, sign.params);
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return name + Arrays.toString(params);
		}
	}
}
