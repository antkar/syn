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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EBNF to BNF converter.
 */
class EbnfToBnfConverter {
	static final String RESULT_KEY = "result";
	
	private final Map<String, BnfNonterminal> nameToNtMap = new HashMap<>();
	private final Map<String, int[]> nameToAnonymousNtCountMap = new HashMap<>();
	private final Map<TokenDescriptor, BnfTerminal> terminalMap = new HashMap<>();
	private final List<BnfNonterminal> nonterminals = new ArrayList<>();
	
	private int prCount = 0;
	private int elCount = 0;
	
	private EbnfToBnfConverter(){}

	/**
	 * Converts an EBNF grammar into a BNF grammar.
	 * 
	 * @param eGrammar the EBNF grammar.
	 * @return the BNF grammar.
	 * @throws SynException if conversion fails.
	 */
	static BnfGrammar convert(EbnfGrammar eGrammar) throws SynException {
		EbnfToBnfConverter converter = new EbnfToBnfConverter();
		
		//Convert all terminal symbols. This fragment ensures that all terminal symbols defined
		//in the EBNF grammar will be converted to BNF. Otherwise, only terminals used in
		//productions reachable from start nonterminals would be converted.
		for (EbnfTerminalElement eTerminal : eGrammar.getTerminals()) {
			TokenDescriptor tokenDescriptor = eTerminal.getTokenDescriptor();
			converter.convertTerminal(tokenDescriptor);
		}
		
		//Convert start nonterminals and all related nonterminals.
		List<BnfNonterminal> bStartNonterminals = new ArrayList<>();
		for (EbnfNonterminal eNonterminal : eGrammar.getStartNonterminals()) {
			BnfNonterminal bNonterminal = converter.convertNonterminal(eNonterminal);
			bStartNonterminals.add(bNonterminal);
		}
		
		//Create and return a BNF grammar instance.
		Collection<BnfTerminal> terminals = converter.terminalMap.values();
		BnfGrammar bGrammar = new BnfGrammar(bStartNonterminals, converter.nonterminals, terminals);
		return bGrammar;
	}
	
	/**
	 * Converts an EBNF nonterminal to BNF. If the nonterminal has already been converted, an existing
	 * BNF nonterminal is returned.
	 */
	BnfNonterminal convertNonterminal(EbnfNonterminal eNonterminal) throws SynException {
		assert eNonterminal != null;
		
		String name = eNonterminal.getName();
		BnfNonterminal bNonterminal = nameToNtMap.get(name);
		if (bNonterminal == null) {
			//Not converted yet. Create a new BNF nonterminal.
			bNonterminal = newNonterminal(name);
			//Put the new BNF nonterminal into the map now - before converting productions - in order
			//to handle recursive nonterminals correctly.
			nameToNtMap.put(name, bNonterminal);
			
			//Convert productions.
			List<EbnfProduction> eProductions = eNonterminal.getProductions().asList();
			List<BnfProduction> bProductions = convertProductions(name, eProductions, false);
			bNonterminal.setProductions(bProductions);
		}
		
		return bNonterminal;
	}
	
	/**
	 * Converts an EBNF terminal to a BNF terminal. If the terminal has already been converted, the
	 * existing BNF terminal is returned.
	 */
	BnfTerminal convertTerminal(TokenDescriptor tokenDescriptor) {
		assert tokenDescriptor != null;
		
		BnfTerminal bTerminal = terminalMap.get(tokenDescriptor);
		if (bTerminal == null) {
			bTerminal = new BnfTerminal(elCount, tokenDescriptor);
			++elCount;
			terminalMap.put(tokenDescriptor, bTerminal);
		}
		
		return bTerminal;
	}
	
	/**
	 * Creates a new anonymous BNF nonterminal. An anonymous BNF nonterminal does not have a corresponding
	 * EBNF nonterminal.  
	 */
	BnfNonterminal createAnonymousNonterminal(String ownerNtName) {
		String name = ownerNtName + "@" + getNextAnonymousNonterminalIndex(ownerNtName);
		BnfNonterminal bNonterminal = newNonterminal(name);
		return bNonterminal;
	}
	
	/**
	 * Creates a new anonymous BNF nonterminal, defining its productions.
	 */
	BnfNonterminal createAnonymousNonterminal(String ownerNtName, List<BnfProduction> bProductions) {
		assert bProductions != null;
		
		BnfNonterminal bNonterminal = createAnonymousNonterminal(ownerNtName);
		bNonterminal.setProductions(bProductions);
		
		return bNonterminal;
	}
	
	/**
	 * Returns the next free anonymous nonterminal index for the specified name.
	 */
	private int getNextAnonymousNonterminalIndex(String name) {
		int[] cnt = nameToAnonymousNtCountMap.get(name);
		if (cnt == null) {
			cnt = new int[1];
			nameToAnonymousNtCountMap.put(name, cnt);
		}
		
		int result = cnt[0];
		++cnt[0];
		return result;
	}
	
	/**
	 * Creates a BNF production from the specified elements. Maintains production index.
	 */
	BnfProduction createProduction(IParserAction parserAction, BnfElement... bElements) {
		List<BnfElement> bElementList = Arrays.asList(bElements);
		BnfProduction bProduction = new BnfProduction(prCount, bElementList, parserAction);
		++prCount;
		return bProduction;
	}

	/**
	 * Converts a list of EBNF productions to a list of BNF productions.
	 * 
	 * @param currentNt the name of the EBNF nonterminal which the EBNF productions belong to.
	 * @param eProductions EBNF productions.
	 * @param forceObjectResult <code>true</code> if the created BNF productions must return an
	 * embedded object value (used for embedded objects).
	 */
	List<BnfProduction> convertProductions(
			String currentNt,
			List<EbnfProduction> eProductions,
			boolean forceObjectResult) throws SynException
	{
		assert eProductions != null;
		
		List<BnfProduction> bProductions = new ArrayList<>();
		for (EbnfProduction eProduction : eProductions) {
			BnfProduction bProduction = convertProduction(currentNt, eProduction, forceObjectResult);
			bProductions.add(bProduction);
		}
		
		return bProductions;
	}
	
	/**
	 * Converts a list of EBNF productions to an anonymous BNF nonterminal.
	 */
	BnfNonterminal convertProductionsToNonterminal(
			String currentNt,
			List<EbnfProduction> eProductions,
			boolean forceObjectResult) throws SynException
	{
		List<BnfProduction> bProductions = convertProductions(currentNt, eProductions, forceObjectResult);
		BnfNonterminal bNonterminal = createAnonymousNonterminal(currentNt, bProductions);
		return bNonterminal;
	}
	
	/**
	 * Converts an EBNF production to a BNF production.
	 */
	private BnfProduction convertProduction(
			String currentNt,
			EbnfProduction eProduction,
			boolean forceObjectResult) throws SynException
	{
		ConvertedElements convertedElements = convertElements(currentNt, eProduction.getElements());
		IParserAction parserAction = convertedElements.createParserAction(forceObjectResult);
		BnfProduction bProduction = new BnfProduction(prCount, convertedElements.bElements, parserAction);
		++prCount;
		return bProduction;
	}
	
	/**
	 * Converts a list of EBNF elements to a list of BNF elements.
	 */
	private ConvertedElements convertElements(String currentNt, List<EbnfElement> eElements) throws SynException 
	{
		//Ensure that there are no attribute conflicts.
		checkAttributeDuplications(eElements);

		Map<String, Getter> getterMap = new HashMap<>();
		Collection<Getter> embeddedGetters = new ArrayList<>();
		IParserGetter defaultNonValuableGetter = null;
		IParserGetter defaultValuableGetter = null;

		//Go through all elements, convert them to BNF and determine the production's result.
		List<BnfElement> bElements = new ArrayList<>();
		for (int i = 0, n = eElements.size(); i < n; ++i) {
			//The elements are processed in a reverse order, because this allows to track the stack
			//offset of a BNF element related to the end of the list of BNF elements.
			
			EbnfElement eElement = eElements.get(n - 1 - i);
			int offset = bElements.size();

			//Obtain a getter which may be needed by a parser action to read the value produced by
			//the BNF element from the stack.
			IParserGetter parserGetter = eElement.getGetter(offset);
			assert parserGetter != null;
			
			//Handle BNF element's return value.
			String attribute = eElement.getAttribute();
			if (attribute != null) {
				//Attribute is specified. Put the getter for the current element into the getters map.
				Getter getter = new Getter(eElement.getAttributePos(), parserGetter);
				getterMap.put(attribute, getter);
			} else if (eElement.hasEmbeddedObject()) {	
				//Attribute is not specified, but the element produces an embedded object.
				Getter getter = new Getter(eElement.getAttributePos(), parserGetter);
				embeddedGetters.add(getter);
			} else if (eElement.isValuableElement()) {
				//Neither attribute, nor embedded object, but the element is valuable. Use it as the
				//default return value for the production.
				defaultValuableGetter = parserGetter;
			} else {
				//None.
				defaultNonValuableGetter = parserGetter;
			}

			//Convert the element's structure.
			BnfElement bElement = eElement.convert(this, currentNt);
			if (bElement != null) {
				//Not all EBNF elements produce a BNF element.
				bElements.add(bElement);
			}
		}
		
		//Reverse the list of elements, since it was build in a reverse order.
		Collections.reverse(bElements);
		
		//Create and return a result object.
		IParserGetter defaultGetter =
				defaultValuableGetter != null ? defaultValuableGetter : defaultNonValuableGetter;
		
		ConvertedElements result = new ConvertedElements(
				bElements,
				defaultGetter,
				getterMap,
				embeddedGetters);
		
		return result;
	}
	
	/**
	 * Checks whether all attributes, including ones used in embedded elements, are unique.
	 * 
	 * @param eElements the list of elements.
	 * @throws SynGrammarException 
	 */
	private static void checkAttributeDuplications(List<EbnfElement> eElements) throws SynGrammarException {
		Set<String> outerAttrs = new HashSet<>();
		Set<String> innerAttrs = new HashSet<>();
		checkConflictingAttributes(outerAttrs, innerAttrs, eElements);
	}
	
	/**
	 * Checks if there are conflicting attributes.
	 * @param outerAttrs attributes defined in outer productions, but that belong to the same scope.  
	 * @param innerAttrs attributes defined in the passed elements will be added to this set.
	 * @param eElements the list of elements to be checked.
	 * @throws SynGrammarException if attributes conflict is detected.
	 */
	private static void checkConflictingAttributes(
			Set<String> outerAttrs,
			Set<String> innerAttrs,
			List<EbnfElement> eElements) throws SynGrammarException
	{
		//Set of attributes defined in passed elements.
		Set<String> attrs = new HashSet<>();
		
		for (EbnfElement eElement : eElements) {
			String attr = eElement.getAttribute();
			if (attr != null) {
				if (outerAttrs.contains(attr) || attrs.contains(attr)) {
					throw new SynGrammarException(eElement.getAttributePos(), "Duplicated attribute: " + attr);
				}
				attrs.add(attr);
			} else if (eElement.hasEmbeddedObject()) {
				EbnfProductions eProductions = eElement.getEmbeddedProductions();
				checkConflictingAttributesForEmbeddedProductions(outerAttrs, attrs, eProductions);
			}
		}
		
		//Add attributes defined in the passed elements to the passed set.
		innerAttrs.addAll(attrs);
	}

	/**
	 * Checks for conflicting attributes in embedded productions.
	 * 
	 * @param outerAttrs attributes, defined in outer productions, but in the same scope. 
	 * @param innerAttrs new attributes will be added there.
	 * @param eProductions
	 * @throws SynGrammarException
	 */
	private static void checkConflictingAttributesForEmbeddedProductions(
			Set<String> outerAttrs,
			Set<String> innerAttrs,
			EbnfProductions eProductions) throws SynGrammarException
	{
		//Outer attributes from an embedded production's point of view.
		Set<String> subOuterAttrs = new HashSet<>();
		subOuterAttrs.addAll(outerAttrs);
		subOuterAttrs.addAll(innerAttrs);
		
		//Attributes, defined in embedded productions.
		Set<String> subInnerAttrs = new HashSet<>();
		
		List<EbnfProduction> eProductionsList = eProductions.asList();
		for (EbnfProduction eProduction : eProductionsList) {
			List<EbnfElement> eSubElements = eProduction.getElements();
			checkConflictingAttributes(subOuterAttrs, subInnerAttrs, eSubElements);
		}
		
		innerAttrs.addAll(subInnerAttrs);
	}

	/**
	 * Creates a new BNF nonterminal with the specified name.
	 */
	private BnfNonterminal newNonterminal(String name) {
		int ntIndex = nonterminals.size();
		BnfNonterminal result = new BnfNonterminal(elCount, ntIndex, name);
		nonterminals.add(result);
		++elCount;
		return result;
	}
	
	/**
	 * BNF elements with additional information about their semantics.
	 */
	private static class ConvertedElements {
		/** BNF elements. */
		final List<BnfElement> bElements;
		
		/** Default result value getter. */
		final IParserGetter defaultGetter;
		
		/** Map of object field getters (can be empty). */
		final Map<String, Getter> getterMap;
		
		/** Collection of embedded object getters (can be empty). */
		final Collection<Getter> embeddedGetters;

		ConvertedElements(
				List<BnfElement> elements,
				IParserGetter defaultGetter,
				Map<String, Getter> getterMap,
				Collection<Getter> embeddedGetters) 
		{
			assert elements != null;
			assert getterMap != null;
			
			bElements = elements;
			this.defaultGetter = defaultGetter;
			this.getterMap = getterMap;
			this.embeddedGetters = embeddedGetters;
		}
		
		/**
		 * Creates a parser action for the list of BNF elements represented by this object.
		 * 
		 * @param forceObjectResult <code>true</code> if the action must return an object even if
		 * no attributes are defined.
		 * @return action.
		 */
		IParserAction createParserAction(boolean forceObjectResult) throws SynException {
			if (!getterMap.isEmpty() || !embeddedGetters.isEmpty()) {
				return createActionWithGetters(forceObjectResult);
			}
			
			if (forceObjectResult) {
				return ParserObjectAction.NULL;
			}
			
			if (defaultGetter != null) {
				return new ParserResultAction(defaultGetter);
			}
			
			return ParserNullAction.INSTANCE;
		}

		/**
		 * Creates an action based on attribute getters.
		 */
		private IParserAction createActionWithGetters(boolean forceObjectResult) throws SynGrammarException {
			Getter resultGetter = getterMap.get(RESULT_KEY);
			if (resultGetter != null) {
				//Special result attribute specified.
				return createResultAction(forceObjectResult, resultGetter);
			}
			
			//Create a normal object action.
			Map<String, IParserGetter> map = convertGetterMap(getterMap);
			Collection<IParserGetter> emGetters = convertEmbeddedGetters(embeddedGetters);
			return new ParserObjectAction(map, emGetters);
		}

		/**
		 * Creates a parser action for the special result attribute.
		 */
		private IParserAction createResultAction(boolean forceObjectResult, Getter resultGetter)
				throws SynGrammarException
		{
			if (getterMap.size() > 1 || !embeddedGetters.isEmpty()) {
				throw new SynGrammarException(resultGetter.attributePos, String.format(
						"Special attribute '%s' is specified together with regular attributes",
						RESULT_KEY));
			}
			
			if (forceObjectResult) {
				throw new SynGrammarException(resultGetter.attributePos, String.format(
						"Special attribute '%s' cannot be used in nested productions",
						RESULT_KEY));
			}
			
			return new ParserResultAction(resultGetter.getter);
		}
		
		private static Map<String, IParserGetter> convertGetterMap(Map<String, Getter> map) {
			Map<String, IParserGetter> result = new HashMap<>();
			for (Map.Entry<String, Getter> entry : map.entrySet()) {
				result.put(entry.getKey(), entry.getValue().getter);
			}
			return result;
		}
		
		private static Collection<IParserGetter> convertEmbeddedGetters(Collection<Getter> embeddedGetters) {
			Collection<IParserGetter> result = new ArrayList<>();
			for (Getter getter : embeddedGetters) {
				result.add(getter.getter);
			}
			return result;
		}
	}
	
	/**
	 * Internal getter representation. Contains also the text position where the attribute was defined
	 * in the grammar.
	 */
	private static class Getter {
		private final TextPos attributePos;
		private final IParserGetter getter;

		private Getter(TextPos keyPos, IParserGetter getter) {
			super();
			this.attributePos = keyPos;
			this.getter = getter;
		}
	}
}
