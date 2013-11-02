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

import java.lang.reflect.Field;

/**
 * Primitive <code>int</code> bound type.
 */
class IntBoundType extends BoundType {
	static final BoundType INSTANCE = new IntBoundType();
	
	private IntBoundType(){}

	@Override
	Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
			throws SynBinderException
	{
		int value = extractValue(synNode);
		return value;
	}

	@Override
	BoundType getArrayType(Field field) throws SynBinderException {
		return IntArrayBoundType.INSTANCE;
	}

	static int extractValue(SynNode synNode) throws SynBinderException {
		ValueNode valueNode = (ValueNode) synNode;
		if (valueNode == null) {
			throw new SynBinderException("Cannot bind null value to an int field");
		}
		
		int value = extractValue0(valueNode);
		return value;
	}

	static int extractValue0(ValueNode valueNode) throws SynBinderException {
		long lValue = valueNode.getLong();
		
		if (lValue < Integer.MIN_VALUE || lValue > Integer.MAX_VALUE) {
			throw new SynBinderException(String.format("Cannot bind value %d to an int field", lValue));
		}
		
		int value = (int) lValue;
		return value;
	}
}
