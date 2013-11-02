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
 * Integer value node. Contains a value of type <code>long</code>. The value is out of <code>int</code> range.
 */
class LongValueNode extends ValueNode {
	private final long value;
	
	LongValueNode(PosBuffer pos, long value) {
		super(pos);
		assert value > Integer.MAX_VALUE || value < Integer.MIN_VALUE : value;
		this.value = value;
	}

	@Override
	public SynValueType getValueType() {
		return SynValueType.INTEGER;
	}
	
	@Override
	public int getInt() {
		throw new IllegalStateException(String.valueOf(value));
	}
	
	@Override
	public long getLong() {
		return value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public TokenDescriptor getTokenDescriptor() {
		return TokenDescriptor.INTEGER;
	}
	
	@Override
	public String toString() {
		return getTokenDescriptor() + "(" + getLong() + ")";
	}
}
