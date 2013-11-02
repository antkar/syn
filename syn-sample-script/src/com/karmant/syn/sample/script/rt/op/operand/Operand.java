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
package com.karmant.syn.sample.script.rt.op.operand;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.ValueType;

/**
 * Operand of an arithmetical operator. When an operator has to be evaluated, the operand
 * (or operands) is converted from {@link RValue} to {@link Operand}. There is limited number of
 * operand types, while the number of different {@link RValue} types is quite large.
 */
public abstract class Operand {
	Operand(){}
	
	/**
	 * Creates a boolean operand.
	 */
	public static Operand forBoolean(boolean value) {
		return BooleanOperand.valueOf(value);
	}
	
	/**
	 * Creates an integer operand.
	 */
	public static Operand forLong(long value) {
		return new LongOperand(value);
	}
	
	/**
	 * Creates a floating-point operand.
	 */
	public static Operand forDouble(double value) {
		return new DoubleOperand(value);
	}
	
	/**
	 * Creates a String operand.
	 */
	public static Operand forString(String value) {
		return new StringOperand(value);
	}
	
	/**
	 * Creates an Object operand.
	 */
	public static Operand forObject(Object value) {
		return new ObjectOperand(value);
	}
	
	/**
	 * Creates a null operand.
	 */
	public static Operand forNull() {
		return NullOperand.INSTANCE;
	}

	/**
	 * Returns the type of the operand.
	 */
	public abstract OperandType getType();
	
	/**
	 * Returns the <code>boolean</code> value of the operand. Throws an exception if the operand
	 * is not boolean.
	 */
	public boolean booleanValue() throws SynsException {
		throw errTypeMissmatch(OperandType.BOOLEAN);
	}
	
	/**
	 * Returns the <code>int</code> value of the operand. Throws an exception if the operand
	 * is not integer or if its value is out of <code>int</code> type range.
	 */
	public int intValue() throws SynsException {
		long v = longValue();
		if (v < Integer.MIN_VALUE || v > Integer.MAX_VALUE) {
			throw new SynsException("Value out of range: " + v);
		}
		return (int)v;
	}
	
	/**
	 * Returns the <code>long</code> value of the operand. Throws an exception if the operand
	 * is not integer.
	 */
	public long longValue() throws SynsException {
		throw errTypeMissmatch(OperandType.LONG);
	}
	
	/**
	 * Returns the <code>double</code> value of the operand. Throws an exception if the operand
	 * is neither floating-point, nor integer.
	 */
	public double doubleValue() throws SynsException {
		throw errTypeMissmatch(OperandType.DOUBLE);
	}
	
	/**
	 * Returns the {@link String} value of the operand. If the operand is not a string, it is
	 * converted to a string.
	 */
	public String stringValue() throws SynsException {
		throw errTypeMissmatch(OperandType.STRING);
	}
	
	/**
	 * Returns the {@link Object} value of the operand. If the operand has a primitive type, a
	 * corresponding wrapper object is returned.
	 */
	public Object objectValue() throws SynsException {
		throw errTypeMissmatch(OperandType.OBJECT);
	}
	
	/**
	 * Explicitly casts the operand to <code>int</code> type. Throws an exception if the operand
	 * is neither integer, nor floating-point.
	 */
	public int castToInt() throws SynsException {
		throw errCastTypeMissmatch(ValueType.INT);
	}
	
	/**
	 * Explicitly casts the operand to <code>long</code> type. Throws an exception if the operand
	 * is neither integer, nor floating-point.
	 */
	public long castToLong() throws SynsException {
		throw errCastTypeMissmatch(ValueType.LONG);
	}
	
	/**
	 * Explicitly casts the operand to <code>double</code> type. Throws an exception if the operand
	 * is neither integer, nor floating-point.
	 */
	public double castToDouble() throws SynsException {
		throw errCastTypeMissmatch(ValueType.DOUBLE);
	}
	
	/**
	 * Throws type mismatch exception.
	 */
	private SynsException errTypeMissmatch(OperandType expectedType) throws SynsException {
		throw SynsException.format(
				"Type missmatch: %s instead of %s",
				getType().getDescriptiveName(),
				expectedType.getDescriptiveName());
	}
	
	/**
	 * Throws explicit cast type mismatch exception.
	 */
	private SynsException errCastTypeMissmatch(ValueType expectedType) {
		return SynsException.format(
				"Cannot cast %s to %s",
				getType().getDescriptiveName(),
				expectedType.getTypeName());
	}
}
