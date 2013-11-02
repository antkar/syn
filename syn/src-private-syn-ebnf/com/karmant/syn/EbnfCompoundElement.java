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
 * Compound EBNF element. An element that contains a set of EBNF productions.
 */
abstract class EbnfCompoundElement extends EbnfElement {
	private final EbnfProductions body;

	EbnfCompoundElement(String key, TextPos keyPos, EbnfProductions body) {
		super(key, keyPos);
		assert body != null;
		this.body = body;
	}
	
	/**
	 * Returns the body productions. (Repetition element, a subclass of this class, has also separator
	 * productions).
	 */
	EbnfProductions getBody() {
		return body;
	}
}
