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

/**
 * The type of a value associated with a {@link ValueNode value node}.
 * 
 * @see ValueNode
 */
public enum SynValueType {
	/**
	 * Boolean. Represented by <code>boolean</code>.
	 */
	BOOLEAN
	{
		@Override
		<T> T invokeProcessor(ValueTypeProcessor<T> processor) throws SynException {
			return processor.processBooleanValue();
		}
	},
	/**
	 * Integer. Represented by either <code>int</code> or <code>long</code>.
	 */
	INTEGER
	{
		@Override
		<T> T invokeProcessor(ValueTypeProcessor<T> processor) throws SynException {
			return processor.processIntegerValue();
		}
	},
	/**
	 * Floating-point. Represented by <code>double</code>.
	 */
	FLOAT
	{
		@Override
		<T> T invokeProcessor(ValueTypeProcessor<T> processor) throws SynException {
			return processor.processFloatValue();
		}
	},
	/**
	 * String. Represented by {@link java.lang.String}.
	 */
	STRING
	{
		@Override
		<T> T invokeProcessor(ValueTypeProcessor<T> processor) throws SynException {
			return processor.processStringValue();
		}
	},
	/**
	 * Object. Represented by {@link java.lang.Object}.
	 */
	OBJECT
	{
		@Override
		<T> T invokeProcessor(ValueTypeProcessor<T> processor) throws SynException {
			return processor.processObjectValue();
		}
	};
	
	/**
	 * Each value invokes a corresponding method of the passed {@link ValueTypeProcessor processor}.
	 * In contrast to using <code>switch</code> statement, this approach ensures that all values are
	 * handled.
	 *  
	 * @param processor the processor. Cannot be <code>null</code>.
	 * @return the value returned by the processor.
	 * @throws SynException if thrown by the processor.
	 */
	abstract <T> T invokeProcessor(ValueTypeProcessor<T> processor) throws SynException;
}
