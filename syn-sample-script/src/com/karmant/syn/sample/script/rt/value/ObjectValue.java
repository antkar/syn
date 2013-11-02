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
package com.karmant.syn.sample.script.rt.value;

import java.util.Map;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.javacls.TypeMatchPrecision;
import com.karmant.syn.sample.script.rt.op.operand.Operand;

/**
 * Script object value.
 */
class ObjectValue extends RValue {
	private final ClassValue classValue;
	private final Map<String, Value> memberValues;
	
	ObjectValue(ClassValue classValue, Map<String, Value> memberValues) {
		this.classValue = classValue;
		this.memberValues = memberValues;
	}
	
	@Override
	public Operand toOperand() throws SynsException {
		return Operand.forObject(this);
	}
	
	@Override
	public ValueType getValueType() {
		return ValueType.OBJECT;
	}

	@Override
	public String getTypeMessage() {
		return getCompoundTypeMessage(classValue.getClassName());
	}

	@Override
	public Value getMemberOpt(String name) {
		Value value = memberValues.get(name);
		if (value == null) {
			value = classValue.getMemberOpt(name);
		}
		return value;
	}
	
	@Override
	public Object toJava(Class<?> type, TypeMatchPrecision precision) {
		if (!type.equals(Object.class)) {
			return INVALID;
		}
		return this;
	}
}
