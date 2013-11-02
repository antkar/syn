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

/**
 * An object that tracks how exactly types of actual arguments of a method match
 * the types of the method's formal parameters. Used to choose an appropriate overloaded method.
 */
public interface TypeMatchPrecision {
	TypeMatchPrecision NULL = new NullTypeMatchPrecision();
	
	/**
	 * Increments the precision. The higher the value - the more suitable is the method.
	 */
	void increment(int value);
}
