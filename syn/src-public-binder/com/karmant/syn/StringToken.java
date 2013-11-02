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
 * Token value of type {@link String}.
 */
public class StringToken extends AbstractToken {
	private final String value;

	public StringToken(TextPos pos, String value) {
		super(pos);
		this.value = value;
	}
	
	/**
	 * Returns the {@link String} value of this object.
	 * @return the value.
	 */
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
