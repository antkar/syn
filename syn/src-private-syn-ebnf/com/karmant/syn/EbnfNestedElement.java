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
 * Nested EBNF element. Simply a set of productions enclosed in parentheses.
 */
class EbnfNestedElement extends EbnfEmbeddedElement {
	EbnfNestedElement(String key, TextPos keyPos, EbnfProductions body) {
		super(key, keyPos, body);
	}

	@Override
	BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException {
		boolean hasEmbeddedObject = hasEmbeddedObject();
		BnfNonterminal bNonterminal = converter.convertProductionsToNonterminal(
				currentNt, getBody().asList(), hasEmbeddedObject);
		return bNonterminal;
	}

	@Override
	<T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
		return processor.processNestedElement(this);
	}
}
