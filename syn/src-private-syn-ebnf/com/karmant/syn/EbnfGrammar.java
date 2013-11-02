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
 * EBNF grammar.
 */
class EbnfGrammar {
	private final List<EbnfNonterminal> startNonterminals;
	private final List<EbnfTerminalElement> terminals;

	EbnfGrammar(List<EbnfNonterminal> startNonterminals, List<EbnfTerminalElement> terminals) {
		assert startNonterminals != null;
		assert !startNonterminals.isEmpty();
		
		this.startNonterminals = Collections.unmodifiableList(new ArrayList<>(startNonterminals));
		this.terminals = Collections.unmodifiableList(new ArrayList<>(terminals));
	}
	
	/**
	 * Returns the list of start nonterminals defined in the grammar.
	 */
	List<EbnfNonterminal> getStartNonterminals() {
		return startNonterminals;
	}
	
	/**
	 * Returns the list of terminal elements defined in the grammar.
	 */
	List<EbnfTerminalElement> getTerminals() {
		return terminals;
	}
}
