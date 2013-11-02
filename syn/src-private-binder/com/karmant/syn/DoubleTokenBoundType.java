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
 * {@link DoubleToken} bound type.
 */
class DoubleTokenBoundType extends AbstractTokenBoundType {
	static final BoundType INSTANCE = new DoubleTokenBoundType();

	private DoubleTokenBoundType() {
		super(DoubleToken.class);
	}

	@Override
	AbstractToken createToken(TextPos pos, ValueNode valueNode) {
		double value = valueNode.getFloat();
		return new DoubleToken(pos, value);
	}
}
