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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates an {@link ObjectBinder}.
 */
class ObjectBinderConfigurator {
	
	private final Class<?> cls;
	private final String ntName;
	
	private final Map<String, Class<?>> ntNameToNtClassMap;
	private final Map<String, List<Class<?>>> ntNameToAllowedClssMap;
	private final Map<Class<?>, Map<String, Class<?>>> ownerClsToOwnedClsMap;
	
	ObjectBinderConfigurator(
			Class<?> cls,
			String ntName,
			Map<String, Class<?>> ntNameToNtClassMap,
			Map<String, List<Class<?>>> ntNameToAllowedClssMap,
			Map<Class<?>, Map<String, Class<?>>> ownerClsToOwnedClsMap)
	{
		this.cls = cls;
		this.ntName = ntName;
		this.ntNameToNtClassMap = Collections.unmodifiableMap(ntNameToNtClassMap);
		this.ntNameToAllowedClssMap = Collections.unmodifiableMap(ntNameToAllowedClssMap);
		this.ownerClsToOwnedClsMap = ownerClsToOwnedClsMap;
	}

	/**
	 * Creates an {@link ObjectBinder} for the given production.
	 */
	ObjectBinder processProduction(
			EbnfProduction production,
			Set<String> definedFields) throws SynException
	{
		//Process elements of the production, collect field binders.
		List<FieldBinder> fieldBinders = new ArrayList<>();
		processProduction0(production, definedFields, false, fieldBinders);
		
		//Ensure that there are no field binder conflicts.
		List<FieldBinder> uniqueFieldBinders = getUniqueFieldBindersList(fieldBinders);
		
		//Create an object binder.
		ObjectBinder objectBinder = new ObjectBinder(cls, uniqueFieldBinders);
		return objectBinder;
	}

	/**
	 * Returns the list of unique field binders. If there is more than one field binder with the same
	 * name in the specified list, and if those binders are incompatible, an exception is thrown.
	 * Otherwise, only one of the binders is included into the resulting list.
	 */
	private List<FieldBinder> getUniqueFieldBindersList(List<FieldBinder> fieldBinders)
			throws SynBinderException
	{
		List<FieldBinder> uniqueFieldBinders = new ArrayList<>();
		Map<String, FieldBinder> fieldNameToFieldBinderMap = new HashMap<>();
		
		for (FieldBinder fieldBinder : fieldBinders) {
			String fieldName = fieldBinder.getFieldName();
			FieldBinder otherFieldBinder = fieldNameToFieldBinderMap.get(fieldName);
			
			if (otherFieldBinder == null) {
				fieldNameToFieldBinderMap.put(fieldName, fieldBinder);
				uniqueFieldBinders.add(fieldBinder);
			} else {
				if (!fieldBinder.isTheSame(otherFieldBinder)) {
					throw new SynBinderException(String.format(
							"Nonterminal %s: field '%s' is bound more than once to incompatible values",
							ntName, fieldName));
				}
			}
		}
		
		return uniqueFieldBinders;
	}
	
	/**
	 * Processes elements of a production, collects field binders.
	 */
	private void processProduction0(
			EbnfProduction production,
			Set<String> definedFields,
			boolean embedded,
			List<FieldBinder> fieldBinders) throws SynException
	{
		Map<String, Collection<Field>> fieldsMap = getFieldsMapForClass(cls);
		
		for (EbnfElement element : production.getElements()) {
			String attribute = element.getAttribute();
			if (attribute != null) {
				processAttributedElement(
						element, attribute, definedFields, embedded, fieldBinders, fieldsMap);
			} else {
				processEmbeddedElements(element, definedFields, fieldBinders);
			}
		}
	}

	/**
	 * Creates field binders for an attributed element.
	 */
	private void processAttributedElement(
			EbnfElement element,
			String attribute,
			Set<String> definedFields,
			boolean embedded,
			List<FieldBinder> fieldBinders,
			Map<String, Collection<Field>> fieldsMap) throws SynException
	{
		Collection<Field> fields = getFieldsForBinding(cls, fieldsMap, attribute);
		for (Field field : fields) {
			definedFields.add(field.getName());
			FieldBinder fieldBinder = createFieldBinder(attribute, field, element, embedded);
			fieldBinders.add(fieldBinder);
		}
	}

	/**
	 * Creates field binders for embedded elements.
	 */
	private void processEmbeddedElements(
			final EbnfElement element0,
			final Set<String> definedKeys,
			final List<FieldBinder> fieldBinders) throws SynException
	{
		element0.invokeProcessor(new EbnfElementProcessor<Void>() {
			@Override
			public Void processValueElement(EbnfValueElement element) {
				return null;
			}
			
			@Override
			public Void processTerminalElement(EbnfTerminalElement element) {
				return null;
			}
			
			@Override
			public Void processRepetitionElement(EbnfRepetitionElement element) {
				return null;
			}
			
			@Override
			public Void processOptionalElement(EbnfOptionalElement element) throws SynException {
				processEmbeddedElement(element, definedKeys, fieldBinders);
				return null;
			}
			
			@Override
			public Void processNonterminalElement(EbnfNonterminalElement element) {
				return null;
			}
			
			@Override
			public Void processNestedElement(EbnfNestedElement element) throws SynException {
				processEmbeddedElement(element, definedKeys, fieldBinders);
				return null;
			}
		});
	}

	/**
	 * Creates field binder(s) for an embedded element.
	 */
	private void processEmbeddedElement(
			EbnfEmbeddedElement element,
			Set<String> definedKeys,
			final List<FieldBinder> fieldBinders) throws SynException
	{
		EbnfProductions bodyProductions = element.getBody();
		List<EbnfProduction> bodyProductionsList = bodyProductions.asList();
		for (EbnfProduction bodyProduction : bodyProductionsList) {
			processProduction0(bodyProduction, definedKeys, true, fieldBinders);
		}
	}
	
	/**
	 * Creates a field binder for the specified Java field and the EBNF element.
	 */
	private FieldBinder createFieldBinder(
			final String key,
			final Field field,
			EbnfElement element,
			boolean embedded) throws SynException
	{
		EbnfElementProcessor<BoundType> processor = new FieldBindingEbnfElementProcessor(
				ntName,
				key,
				field,
				embedded,
				ntNameToNtClassMap,
				ntNameToAllowedClssMap,
				ownerClsToOwnedClsMap);
		
		BoundType boundType = element.invokeProcessor(processor);
		return new FieldBinder(key, field, embedded, boundType);
	}
	
	/**
	 * Returns the collection of Java fields that have to be bound to the specified grammar attribute.
	 * More than one Java field can be bound to a single attribute.
	 * @see {@link SynField}
	 */
	private static Collection<Field> getFieldsForBinding(
			Class<?> cls,
			Map<String, Collection<Field>> keyToFieldMap,
			String attribute) throws SynBinderException
	{
		Collection<Field> fields = keyToFieldMap.get(attribute);
		
		if (fields == null || fields.isEmpty()) {
			throw new SynBinderException(String.format(
					"Class %s does not have a field '%s'",
					cls.getCanonicalName(), attribute));
		}
		
		return fields;
	}
	
	/**
	 * Builds a map which maps a grammar attribute name to a collection of Java fields where that
	 * attribute has to be bound to.
	 */
	private static Map<String, Collection<Field>> getFieldsMapForClass(Class<?> cls)
			throws SynBinderException
	{
		Map<String, Collection<Field>> map = new HashMap<>();
		
		Class<?> curCls = cls;
		while (curCls != null) {
			getFieldsForConcreteClass(curCls, cls, map);
			curCls = curCls.getSuperclass();
		}
		
		return map;
	}

	/**
	 * Examines attribute-to-fields mapping for a Java class. Inherited fields are not taken into account.
	 */
	private static void getFieldsForConcreteClass(
			Class<?> cls,
			Class<?> initialCls,
			Map<String, Collection<Field>> map) throws SynBinderException
	{
		for (Field field : cls.getDeclaredFields()) {
			SynField synField = field.getAnnotation(SynField.class);
			if (synField != null) {
				verifyBoundField(initialCls, field);
				
				String attribute = synField.value();
				if (attribute.isEmpty()) {
					attribute = field.getName();
				}
				
				CommonUtil.addToCollectionMap(map, attribute, field);
			}
		}
	}

	/**
	 * Checks whether the specified Java field satisfies Binder limitations.
	 */
	private static void verifyBoundField(Class<?> cls, Field field) throws SynBinderException {
		int modifiers = field.getModifiers();
		
		if (Modifier.isStatic(modifiers)) {
			throw new SynBinderException(String.format(
					"Field '%s' of class %s is static",
					field.getName(), cls.getCanonicalName()));
		}
		
		if (Modifier.isFinal(modifiers)) {
			throw new SynBinderException(String.format(
					"Field '%s' of class %s is final",
					field.getName(), cls.getCanonicalName()));
		}
	}
}
