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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser configuration. Defines LR tables and other information used by {@link ParserEngine} to
 * parse a text.
 */
class ParserConfiguration {
	private final Map<String, ParserState> startStates;
	private final List<ParserState> states;
	private final List<TokenDescriptor> tokenDescriptors;

	ParserConfiguration(
			Map<String, ParserState> startStates,
			List<ParserState> states,
			List<TokenDescriptor> tokenDescriptors)
	{
		assert startStates != null;
		assert states != null;
		assert tokenDescriptors != null;

		this.startStates = Collections.unmodifiableMap(new HashMap<>(startStates));
		this.states = Collections.unmodifiableList(new ArrayList<>(states));
		this.tokenDescriptors = Collections.unmodifiableList(new ArrayList<>(tokenDescriptors));
	}
	
	/**
	 * Returns the start LR state for a given start nonterminal.
	 */
	ParserState getStartState(String name) {
		ParserState result = startStates.get(name);
		return result;
	}
	
	/**
	 * Returns the list of token descriptors used in the grammar.
	 */
	List<TokenDescriptor> getTokenDescriptors() {
		return tokenDescriptors;
	}
	
	/**
	 * Prints LR states to a stream. For debug purposes.
	 */
	void print(PrintStream out) {
		for (ParserState state : states) {
			state.print(out);
		}
	}
}
