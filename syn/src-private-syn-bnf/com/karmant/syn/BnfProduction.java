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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BNF production.
 */
class BnfProduction {
	private final int index;
	private BnfNonterminal nonterminal;
	private final List<BnfElement> elements;
	private final IParserAction parserAction;

	BnfProduction(int index, List<BnfElement> elements, IParserAction parserAction) {
		assert index >= 0;
		assert elements != null;
		assert parserAction != null;
		
		this.index = index;
		this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
		this.parserAction = parserAction;
		
		nonterminal = null;
	}
	
	/**
	 * Sets the nonterminal which this production belongs to. This cannot be done in the constructor, because
	 * there can be circular dependencies between nonterminals.
	 */
	void setNonterminal(BnfNonterminal nonterminal) {
		assert nonterminal != null;
		assert this.nonterminal == null;
		this.nonterminal = nonterminal;
	}
	
	/**
	 * Returns the unique index of this production in the grammar.
	 */
	int getIndex() {
		return index;
	}
	
	/**
	 * Returns the nonterminal which this production belongs to.
	 */
	BnfNonterminal getNonterminal() {
		assert nonterminal != null;
		return nonterminal;
	}

	/**
	 * Returns the elements of the production.
	 */
	List<BnfElement> getElements() {
		return elements;
	}
	
	/**
	 * Returns the action associated with the production.
	 */
	IParserAction getParserAction() {
		return parserAction;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		
		//Append elements.
		for (BnfElement e : elements) {
			if (bld.length() > 0) {
				bld.append(" ");
			}
			String es = e.toString();
			bld.append(es);
		}
		
		//Append action.
		if (parserAction != null) {
			if (bld.length() > 0) {
				bld.append(" ");
			}
			bld.append("{ ");
			bld.append(parserAction.toString());
			bld.append(" }");
		}
		
		String result = bld.toString();
		return result;
	}
}
