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
 * {@link Integer} bound type.
 */
class IntegerBoundType extends AbstractBoundType {
	static final BoundType INSTANCE = new IntegerBoundType();

	private IntegerBoundType() {
		super(Integer.class);
	}

	@Override
	Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
			throws SynBinderException
	{
		ValueNode valueNode = (ValueNode) synNode;
		if (valueNode == null) {
			return null;
		}
		return IntBoundType.extractValue0(valueNode);
	}
}
