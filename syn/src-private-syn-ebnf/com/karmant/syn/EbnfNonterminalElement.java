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
 * Nonterminal EBNF element.
 */
class EbnfNonterminalElement extends EbnfElement {
	private final EbnfNonterminal nonterminal;

	EbnfNonterminalElement(String key, TextPos keyPos, EbnfNonterminal nonterminal) {
		super(key, keyPos);
		assert nonterminal != null;
		this.nonterminal = nonterminal;
	}
	
	/**
	 * Returns the nonterminal referenced by this element.
	 */
	EbnfNonterminal getNonterminal() {
		return nonterminal;
	}

	@Override
	BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException {
		BnfNonterminal bNonterminal = converter.convertNonterminal(nonterminal);
		return bNonterminal;
	}
	
	@Override
	public String toString() {
		return nonterminal.toString();
	}

	@Override
	<T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
		return processor.processNonterminalElement(this);
	}
}
