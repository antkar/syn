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

import java.util.HashMap;
import java.util.Map;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * A wrapper for a Java class which provides an access to the class' members as to {@link Value}s.
 */
public class JavaClass {
	private static final Map<Class<?>, JavaClass> CLASSES_MAP = new HashMap<>();
	
	private final Class<?> javaCls;
	private final JavaMethodSet constructors;
	private final Map<String, JavaMember> membersMap;
	
	private JavaClass(Class<?> cls) {
		synchronized (CLASSES_MAP) {
			CLASSES_MAP.put(cls, this);
		}
		
		javaCls = cls;
		
		constructors = JavaClassExplorer.discoverConstructors(cls);
		membersMap = JavaClassExplorer.discoverFieldsAndMethods(cls);
	}

	/**
	 * Returns a {@link JavaClass} instance for the specified Java class.
	 */
	public static JavaClass getInstance(Class<?> cls) {
		synchronized (CLASSES_MAP) {
			JavaClass javaClass = CLASSES_MAP.get(cls);
			if (javaClass == null) {
				javaClass = new JavaClass(cls);
			}
			return javaClass;
		}
	}
	
	/**
	 * Returns the associated Java class.
	 */
	public Class<?> getJavaClass() {
		return javaCls;
	}
	
	/**
	 * Returns the value of the specified static member, or <code>null</code>.
	 */
	public Value getStaticMemberOpt(String name) throws SynsException {
		JavaMember member = membersMap.get(name);
		if (member == null) {
			return null;
		}
		
		return member.getStaticValue();
	}
	
	/**
	 * Returns the value of the specified instance member, or <code>null</code>.
	 */
	public Value getInstanceMemberOpt(String name, Object obj) {
		JavaMember member = membersMap.get(name);
		if (member == null) {
			return null;
		}
		
		return member.getInstanceValue(obj);
	}

	/**
	 * Creates a new instance of the Java class, wraps it and returns the resulting {@link Value}.
	 */
	public Value newInstance(RValue[] arguments) throws SynsException {
		return constructors.callStatic(arguments);
	}
	
	@Override
	public String toString() {
		return javaCls.toString();
	}
}
