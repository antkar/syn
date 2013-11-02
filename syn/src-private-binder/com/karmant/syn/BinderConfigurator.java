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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates a {@link BinderConfiguration}.
 */
final class BinderConfigurator {
	
	private final Map<String, BindingNonterminal> bindingNtsMap;
	
	private final Map<String, Class<?>> ntNameToPrClassMap = new HashMap<>();
	private final Map<String, Class<?>> ntNameToNtClassMap = new HashMap<>();
	private final Map<String, List<Class<?>>> ntNameToAllowedClssMap = new HashMap<>();
	private final Map<String, ObjectBinder> prKeyToBinderMap = new HashMap<>();
	private final Map<Class<?>, Map<String, Class<?>>> ownerClsToOwnedClsMap = new HashMap<>();
	private final Map<Class<?>, Collection<InitMethod>> clsToInitMethodsMap = new HashMap<>();
	private Map<Class<?>, Collection<Lookup>> clsToLookupsMap;
	
	private BinderConfigurator(Map<String, BindingNonterminal> bindingNtsMap) {
		this.bindingNtsMap = bindingNtsMap;
	}
	
	/**
	 * Creates a binder configuration for the given EBNF grammar and the Java class.
	 */
	static <T> BinderConfiguratorResult<T> makeConfiguration(
			Class<T> classToBind,
			EbnfGrammar orgGrammar) throws SynException
	{
		//Create an extended EBNF grammar with integrated binder actions.
		String startNtName = classToBind.getSimpleName();
		BindingGrammarConverter converter =
				BindingGrammarConverter.convertBindingGrammar(startNtName, orgGrammar);
		EbnfGrammar genGrammar = converter.getGenGrammar();
		Map<String, BindingNonterminal> bindingNtsMap = converter.getBindingNtsMap();
		
		//Create a configuration.
		BinderConfigurator configurator = new BinderConfigurator(bindingNtsMap);
		BinderConfiguration<T> config = configurator.createConfiguration(classToBind);
		return new BinderConfiguratorResult<>(genGrammar, config);
	}
	
	/**
	 * Creates a binder configuration based on the given Java class.
	 */
	private <T> BinderConfiguration<T> createConfiguration(Class<T> rootBindClass) throws SynException {
		initNtNameToPrClassMap(rootBindClass);
		initNtNameToAllowedClssMap();
		initNtNameToNtClassMap();
		initPrKeyToBinderMap();
		initLookups();
		initInitMethods();
		
		return new BinderConfiguration<>(
				rootBindClass,
				prKeyToBinderMap,
				clsToLookupsMap,
				clsToInitMethodsMap);
	}

	/**
	 * Initializes the mapping between nonterminals and Java classes.
	 */
	private void initNtNameToPrClassMap(Class<?> rootBindClass) throws SynBinderException {
		ClassLoader classLoader = rootBindClass.getClassLoader();
		String fullName = rootBindClass.getName();
		String simpleName = rootBindClass.getSimpleName();
		String namePreffix = fullName.substring(0, fullName.length() - simpleName.length());
		for (String ntName : bindingNtsMap.keySet()) {
			BindingNonterminal bindingNt = bindingNtsMap.get(ntName);
			if (!bindingNt.getInnerPrs().isEmpty()) {
				String clsName = namePreffix + ntName;
				Class<?> cls = BinderReflectionUtil.getPrClassByName(classLoader, clsName);
				ntNameToPrClassMap.put(ntName, cls);
			}
		}
	}
	
	/**
	 * Defines which Java classes are acceptable by each nonterminal.
	 */
	private void initNtNameToAllowedClssMap() throws SynBinderException {
		Map<String, Set<String>> directAllowedNtsMap = buildDirectAllowedNtsMap();
		Map<String, Set<String>> fullAllowedNtsMap = buildFullAllowedNtsMap(directAllowedNtsMap);
		fillNtNameToAllowedClssMap(fullAllowedNtsMap);
	}

	/**
	 * Builds map which maps a nonterminal to a set of all directly allowed nonterminals for that
	 * nonterminal. A nonterminal B is allowed for a nonterminal A if A has B as a production. 
	 */
	private Map<String, Set<String>> buildDirectAllowedNtsMap() {
		Map<String, Set<String>> directAllowedNtsMap = new HashMap<>();
		
		for (String ntName : bindingNtsMap.keySet()) {
			Set<String> allowedNts = getDirectAllowedNtsForNt(ntName);
			directAllowedNtsMap.put(ntName, allowedNts);
		}
		
		return directAllowedNtsMap;
	}

	/**
	 * Returns the set of direct allowed nonterminals for the given nonterminal.
	 */
	private Set<String> getDirectAllowedNtsForNt(String ntName) {
		Set<String> allowedNts = new HashSet<>();
		
		BindingNonterminal bindingNt = bindingNtsMap.get(ntName);
		for (EbnfNonterminal innerNt : bindingNt.getInnerNts()) {
			String innerNtName = innerNt.getName();
			allowedNts.add(innerNtName);
		}
		
		if (!bindingNt.getInnerPrs().isEmpty()) {
			allowedNts.add(ntName);
		}
		
		return allowedNts;
	}

	/**
	 * Builds a full allowed nonterminals map from a direct allowed nonterminals map.
	 */
	private Map<String, Set<String>> buildFullAllowedNtsMap(
			Map<String, Set<String>> directAllowedNtsMap)
	{
		Map<String, Set<String>> fullAllowedNtsMap = new HashMap<>();
		
		for (String ntName : bindingNtsMap.keySet()) {
			Set<String> set = getFullAllowedNtsForNt(ntName, directAllowedNtsMap);
			fullAllowedNtsMap.put(ntName, set);
		}
		
		return fullAllowedNtsMap;
	}

	/**
	 * Returns the full set of allowed nonterminals for the given nonterminal.
	 */
	private Set<String> getFullAllowedNtsForNt(
			String ntName,
			Map<String, Set<String>> directAllowedNtsMap)
	{
		Set<String> set = new HashSet<>();
		List<String> queue = new ArrayList<>();
		queue.add(ntName);
		
		for (int pos = 0; pos < queue.size(); ++pos) {
			String curNtName = queue.get(pos);
			Set<String> directAllowedNts = directAllowedNtsMap.get(curNtName);
			for (String allowedNt : directAllowedNts) {
				if (set.add(allowedNt)) {
					queue.add(allowedNt);
				}
			}
		}
		
		return set;
	}

	/**
	 * Fills a map of allowed Java classes for each nonterminal.
	 */
	private void fillNtNameToAllowedClssMap(Map<String, Set<String>> fullAllowedNtsMap)
			throws SynBinderException
	{
		for (String ntName : bindingNtsMap.keySet()) {
			List<Class<?>> allowedClss = getFullAllowedClssListForNt(ntName, fullAllowedNtsMap);
			
			if (allowedClss.isEmpty()) {
				throw new SynBinderException(String.format(
						"Nonterminal %s has no corresponding Java class", ntName));
			}
			
			ntNameToAllowedClssMap.put(ntName, allowedClss);
		}
	}

	/**
	 * Returns the full list of allowed Java classes for a nonterminal.
	 */
	private List<Class<?>> getFullAllowedClssListForNt(
			String ntName,
			Map<String, Set<String>> fullAllowedNtsMap)
	{
		List<Class<?>> allowedClss = new ArrayList<>();
		
		Set<String> fullAllowedNts = fullAllowedNtsMap.get(ntName);
		for (String allowedNt : fullAllowedNts) {
			Class<?> allowedCls = ntNameToPrClassMap.get(allowedNt);
			if (allowedCls != null) {
				allowedClss.add(allowedCls);
			}
		}
		
		return allowedClss;
	}

	/**
	 * Initializes a map of allowed Java classes for each nonterminal.
	 */
	private void initNtNameToNtClassMap() {
		for (String ntName : bindingNtsMap.keySet()) {
			List<Class<?>> allowedClss = ntNameToAllowedClssMap.get(ntName);
			Class<?> topCls = CommonSuperclass.getCommonSuperclass(allowedClss);
			ntNameToNtClassMap.put(ntName, topCls);
		}
	}

	/**
	 * Initializes a map which maps a production to its node binder.
	 */
	private void initPrKeyToBinderMap() throws SynException {
		for (String ntName : ntNameToPrClassMap.keySet()) {
			initPrKeyToBinderMapForNt(ntName);
		}
	}

	/**
	 * Initializes a production-to-binder mapping for a nonterminal.
	 */
	private void initPrKeyToBinderMapForNt(String ntName) throws SynException {
		Class<?> cls = ntNameToPrClassMap.get(ntName);
		
		//Create production binder configurator.
		ObjectBinderConfigurator objectConfigurator = new ObjectBinderConfigurator(
				cls, ntName, ntNameToNtClassMap, ntNameToAllowedClssMap, ownerClsToOwnedClsMap);
		
		Set<String> definedFields = new HashSet<>();
		
		//Process all productions one-by-one.
		BindingNonterminal bindingNt = bindingNtsMap.get(ntName);
		Map<String, EbnfProduction> innerPrs = bindingNt.getInnerPrs();
		for (String prKey : innerPrs.keySet()) {
			EbnfProduction production = innerPrs.get(prKey);
			ObjectBinder objectBinder = objectConfigurator.processProduction(production, definedFields);
			prKeyToBinderMap.put(prKey, objectBinder);
		}
		
		//Ensure that all Java fields are initialized by the production.
		BinderReflectionUtil.ensureAllFieldsAreDefined(cls, definedFields);
	}

	/**
	 * Initializes the lookups map.
	 */
	private void initLookups() throws SynException {
		Collection<Class<?>> bindingClasses = ntNameToPrClassMap.values();
		clsToLookupsMap = LookupInitializer.initializeLookups(bindingClasses, ownerClsToOwnedClsMap);
	}

	/**
	 * Initializes the initialization methods map.
	 */
	private void initInitMethods() throws SynBinderException {
		for (Class<?> cls : ntNameToPrClassMap.values()) {
			Class<?> curCls = cls;
			while (curCls != null) {
				Collection<InitMethod> initMethods = BinderReflectionUtil.getInitMethodsForClass(curCls);
				clsToInitMethodsMap.put(curCls, initMethods);
				curCls = curCls.getSuperclass();
			}
		}
	}
}
