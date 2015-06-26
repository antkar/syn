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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates a meta information necessary to initialize {@link SynLookup} fields.
 */
final class LookupInitializer {
    
    private final Collection<Class<?>> bindingClasses;
    private final Map<Class<?>, Map<String, Class<?>>> ownerToOwnedMap;
    
    private final Map<Class<?>, Map<String, Field>> clsToSynFldMap = new HashMap<>();
    private final Map<Class<?>, Map<String, Field>> clsToLookupFldMap = new HashMap<>();
    private final Map<Class<?>, Collection<Lookup>> clsToLookupsMap = new HashMap<>();
    private final Map<Class<?>, Class<?>> clsToOwnerMap = new HashMap<>();
    
    private LookupInitializer(
            Collection<Class<?>> bindingClasses,
            Map<Class<?>, Map<String, Class<?>>> ownerToOwnedMap)
    {
        Set<Class<?>> clsSet = collectHierarchyClasses(bindingClasses);
        this.bindingClasses = Collections.unmodifiableCollection(clsSet);
        this.ownerToOwnedMap = Collections.unmodifiableMap(ownerToOwnedMap);
    }

    /**
     * Returns the set of all super-classes of the specified classes.
     */
    private static Set<Class<?>> collectHierarchyClasses(Collection<Class<?>> classes) {
        Set<Class<?>> resultSet = new HashSet<>();
        for (Class<?> cls : classes) {
            Class<?> curCls = cls;
            while (curCls != null) {
                resultSet.add(curCls);
                curCls = curCls.getSuperclass();
            }
        }
        return resultSet;
    }

    /**
     * Creates lookups meta information for specified classes.
     */
    static Map<Class<?>, Collection<Lookup>> initializeLookups(
            Collection<Class<?>> bindingClasses,
            Map<Class<?>, Map<String, Class<?>>> ownerToOwnedMap) throws SynException
    {
        LookupInitializer initializer = new LookupInitializer(bindingClasses, ownerToOwnedMap);
        initializer.initializeLookups0();
        return initializer.clsToLookupsMap;
    }
    
    private void initializeLookups0() throws SynException {
        initClsToFldMaps();
        initClsToOwnerMap();
        processLookups();
    }

    /**
     * Initializes class-to-fields maps for all binding classes.
     */
    private void initClsToFldMaps() throws SynBinderException {
        for (Class<?> cls : bindingClasses) {
            initFldMapsForClass(cls);
        }
    }

    /**
     * Initializes class-to-fields maps for the specified class.
     */
    private void initFldMapsForClass(Class<?> cls) throws SynBinderException {
        Map<String, Field> synFldMap = new HashMap<>();
        Map<String, Field> lookupFldMap = new HashMap<>();
        
        for (Field fld : cls.getDeclaredFields()) {
            SynField synField = fld.getAnnotation(SynField.class);
            SynLookup synLookup = fld.getAnnotation(SynLookup.class);
            
            if (synField != null && synLookup != null) {
                throw new SynBinderException(String.format(
                        "Field %s has both %s and %s annotations",
                        fld, SynField.class.getSimpleName(), SynLookup.class.getSimpleName()));
            }
            
            String fldName = fld.getName();
            if (synField != null) {
                synFldMap.put(fldName, fld);
            } else if (synLookup != null) {
                lookupFldMap.put(fldName, fld);
            }
        }
        
        clsToSynFldMap.put(cls, synFldMap);
        clsToLookupFldMap.put(cls, lookupFldMap);
    }

    /**
     * Initializes the class-to-owner map.
     */
    private void initClsToOwnerMap() {
        
        Map<Class<?>, Set<Class<?>>> clsToOwnersMap = buildDirectOwnedToOwnerdMap();
        
        //For each class, add owners of its super-classes to the set of the owners of that class.
        for (Class<?> cls : clsToSynFldMap.keySet()) {
            inheritSuperOwners(cls, clsToOwnersMap);
        }
        
        for (Class<?> ownedCls : clsToOwnersMap.keySet()) {
            Set<Class<?>> ownerSet = clsToOwnersMap.get(ownedCls);
            Class<?> ownerCls = CommonSuperclass.getCommonSuperclass(ownerSet);
            clsToOwnerMap.put(ownedCls, ownerCls);
        }
    }

    /**
     * Adds owner classes of super-classes of the specified class to the set.
     */
    private static void inheritSuperOwners(Class<?> cls, Map<Class<?>, Set<Class<?>>> clsToOwnersMap) {
        Set<Class<?>> ownerSet = new HashSet<>();
        
        Class<?> curCls = cls;
        while (curCls != null) {
            Set<Class<?>> curOwnerSet = clsToOwnersMap.get(curCls);
            if (curOwnerSet != null) {
                ownerSet.addAll(curOwnerSet);
            }
            curCls = curCls.getSuperclass();
        }
        
        if (!ownerSet.isEmpty()) {
            clsToOwnersMap.put(cls, ownerSet);
        }
    }

    /**
     * Builds a direct class-to-owner map.
     */
    private Map<Class<?>, Set<Class<?>>> buildDirectOwnedToOwnerdMap() {
        Map<Class<?>, Set<Class<?>>> clsToOwnersMap = new HashMap<>();
        
        for (Class<?> ownerCls : ownerToOwnedMap.keySet()) {
            Map<String, Class<?>> ownedMap = ownerToOwnedMap.get(ownerCls);
            for (Class<?> ownedCls : ownedMap.values()) {
                CommonUtil.addToSetMap(clsToOwnersMap, ownedCls, ownerCls);
            }
        }
        
        return clsToOwnersMap;
    }

    /**
     * Processes {@link SynLookup} fields and creates a meta information for each of them.
     */
    private void processLookups() throws SynException {
        for (Class<?> cls : clsToLookupFldMap.keySet()) {
            Collection<Lookup> lookups = processLookupsForClass(cls);
            clsToLookupsMap.put(cls, lookups);
        }
    }

    /**
     * Processes {@link SynLookup} fields for the specified class.
     */
    private Collection<Lookup> processLookupsForClass(Class<?> cls) throws SynException {
        Collection<Lookup> lookups = new ArrayList<>();
        
        Map<String, Field> lookupFldMap = clsToLookupFldMap.get(cls);
        for (String fldName : lookupFldMap.keySet()) {
            Field fld = lookupFldMap.get(fldName);
            SynLookup synLookup = fld.getAnnotation(SynLookup.class);
            Lookup lookup = processLookup(cls, fld, synLookup);
            lookups.add(lookup);
        }
        
        return lookups;
    }

    /**
     * Creates a binding meta information for a {@link SynLookup} field.
     */
    private Lookup processLookup(Class<?> cls, Field fld, SynLookup synLookup) throws SynException {
        LookupBinder binder;
        Class<?> clsOfThis = cls;
        Class<?> clsOfObj;
        Class<?> type = fld.getType();
        
        if (type.isArray()) {
            clsOfObj = type.getComponentType();
            binder = new ArrayLookupBinder(fld, clsOfObj);
        } else {
            clsOfObj = type;
            binder = new SingleObjectLookupBinder(fld);
        }
        
        if (!clsToSynFldMap.containsKey(clsOfObj)) {
            throw new SynBinderException(String.format(
                    "Field %s: type %s is not a bound class",
                    fld, clsOfObj.getCanonicalName()));
        }

        String expressionStr = synLookup.value();
        String sourceName = fld.toString();
        LookupExpression expression =
                processLookupExpression(clsOfThis, clsOfObj, expressionStr, sourceName);
        
        Lookup lookup = new Lookup(clsOfObj, expression, binder);
        return lookup;
    }

    /**
     * Parses a lookup expression.
     */
    private LookupExpression processLookupExpression(
            Class<?> clsOfThis,
            Class<?> clsOfObj,
            String expressionStr,
            String sourceName) throws SynException
    {
        SynParser synParser = getLookupParser();
        Reader reader = new StringReader(expressionStr);
        SourceDescriptor sourceDescriptor = new StringSourceDescriptor(sourceName);
        SynResult synResult = synParser.parse("Expression", reader, sourceDescriptor);
        
        SynNode rootNode = synResult.getRootNode();
        LookupExpression expression = ExpressionProcessor.processExpression(
                clsOfThis,
                clsOfObj,
                clsToSynFldMap,
                clsToLookupFldMap,
                clsToOwnerMap,
                rootNode);
        
        return expression;
    }

    private SynParser lookupParser;

    private SynParser getLookupParser() throws SynException {
        if (lookupParser == null) {
            lookupParser = getStaticLookupParser();
        }
        return lookupParser;
    }
    
    private static SynParser LOOKUP_PARSER = null;
    
    /**
     * Returns a lookup parser. (Lazily creates one, if necessary.)
     */
    private static synchronized SynParser getStaticLookupParser() throws SynException {
        if (LOOKUP_PARSER == null) {
            final String fileName = "lookup-expression-grammar.txt";
            try {
                try (Reader reader =
                        CommonUtil.openResourceReader(LookupInitializer.class, fileName))
                {
                    LOOKUP_PARSER = new SynParser(reader);
                }
            } catch (IOException e) {
                throw new SynException(e);
            }
        }
        return LOOKUP_PARSER;
    }
}
