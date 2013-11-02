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
 * EBNF grammar element.
 */
abstract class EbnfElement {
	private final String attribute;
	private final TextPos attributePos;

	EbnfElement(String attribute, TextPos attributePos) {
		this.attribute = attribute;
		this.attributePos = attributePos;
	}

	/**
	 * Returns the attribute of this element. Can be <code>null</code>.
	 */
	String getAttribute() {
		return attribute;
	}
	
	/**
	 * Returns the text position of the attribute in the grammar input. Can be <code>null</code>.
	 */
	TextPos getAttributePos() {
		return attributePos;
	}
	
	/**
	 * Converts this EBNF element to a BNF element.
	 * 
	 * @param converter the converter to use.
	 * @param currentNt the name of the nonterminal which the element belongs to.
	 * 
	 * @return the BNF element.
	 * @throws SynException if the element cannot be converted.
	 */
	abstract BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException;
	
	/**
	 * Each concrete element subclass invokes a corresponding processor's method. This is similar to the
	 * Visitor pattern.
	 */
	abstract <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException;
	
	/**
	 * Returns <code>true</code> if this element produces a value which can be used as the return value
	 * for a production by default.
	 */
	boolean isValuableElement() {
		return true;
	}
	
	/**
	 * Returns a getter object for getting the value of this element.
	 */
	IParserGetter getGetter(int offset) {
		return new ParserStackGetter(offset);
	}

	/**
	 * Returns <code>true</code> if this element contains an embedded object. An embedded object is an object
	 * that can be a part of another object defined by its an outer element.
	 */
	boolean hasEmbeddedObject() {
		return false;
	}
	
	/**
	 * Returns productions embedded into this element. Must be called only if {@link #hasEmbeddedObject()}
	 * returns <code>true</code>.
	 */
	EbnfProductions getEmbeddedProductions() {
		throw new UnsupportedOperationException();
	}
}
