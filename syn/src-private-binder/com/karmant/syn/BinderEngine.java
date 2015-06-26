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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Binder engine. Created by a {@link SynBinder} every time when it needs to bind a new AST to Java classes.
 *
 * @param <T> the type of the start nonterminal.
 */
class BinderEngine<T> {
    
    /** Special attribute injected into productions of an extended grammar to associate
     * a binder with them. */
    static final String PRODUCTION_ATTR = "SynBinder_Production";

    private final List<BoundObject> bObjs = new ArrayList<>();
    private final Map<Class<?>, List<BoundObject>> clsToObjsMap = new HashMap<>();
    
    private final BinderConfiguration<T> config;

    private BinderEngine(BinderConfiguration<T> config) {
        this.config = config;
    }
    
    /**
     * Creates Java objects from the given AST tree root.
     */
    static <T> T createObjects(BinderConfiguration<T> config, SynNode rootNode) throws SynBinderException {
        BinderEngine<T> engine = new BinderEngine<>(config);
        return engine.createObjects0(rootNode);
    }
    
    /**
     * Creates Java objects from an AST tree.
     */
    private T createObjects0(SynNode rootNode) throws SynBinderException {
        
        if (!(rootNode instanceof ObjectNode)) {
            throw new SynBinderException(String.format(
                    "Root of syntax tree is not a %s",
                    ObjectNode.class.getSimpleName()));
        }
        
        //Bind tree nodes recursively.
        ObjectNode objRootNode = (ObjectNode) rootNode;
        BoundObject bObj = bindObjectNode(objRootNode);
        
        //Initialize lookup fields.
        initLookupFields();
        
        //Invoke initialization methods.
        invokeInitMethods();
        
        //Return the root object.
        Object obj = bObj.getJavaObject();
        Class<T> classToBind = config.getClassToBind();
        T result = classToBind.cast(obj);
        
        return result;
    }
    
    /**
     * Creates a Java object for the given AST node and all its sub-nodes.
     */
    BoundObject bindObjectNode(ObjectNode objectNode) throws SynBinderException {
        //Find out the production key.
        String productionKey = objectNode.getString(PRODUCTION_ATTR);
        if (productionKey == null) {
            throw new IllegalStateException("Production attribute is not found in the node");
        }
        
        //Get the binder for the production.
        Map<String, ObjectBinder> prKeyToBinderMap = config.getPrKeyToBinderMap();
        ObjectBinder objectBinder = prKeyToBinderMap.get(productionKey);
        if (objectBinder == null) {
            throw new IllegalStateException(String.format(
                    "Node binder not found for production key '%s'",
                    productionKey));
        }
        
        //Use the binder to create a Java object.
        BoundObject obj = objectBinder.bindNode(this, objectNode);
        return obj;
    }
    
    /**
     * Adds an object to the objects list. The list is used by {@link SynLookup} to search objects.
     */
    void addBObj(BoundObject bObj) {
        bObjs.add(bObj);
        Object obj = bObj.getJavaObject();
        Class<?> cls = obj.getClass();
        CommonUtil.addToListMap(clsToObjsMap, cls, bObj);
    }
    
    /**
     * Returns all created Java objects of the specified class.
     */
    List<BoundObject> getObjsForClass(Class<?> cls) {
        List<BoundObject> list = clsToObjsMap.get(cls);
        if (list == null) {
            list = Collections.emptyList();
        }
        return list;
    }
    
    /**
     * Returns the set of classes of Java objects.
     */
    Collection<Class<?>> getClassesOfObjs() {
        Set<Class<?>> keySet = clsToObjsMap.keySet();
        return keySet;
    }
    
    /**
     * Initializes {@link SynLookup} fields.
     */
    private void initLookupFields() throws SynBinderException {
        for (BoundObject bObj : bObjs) {
            initLookupValuesForObject(bObj);
        }
    }

    /**
     * Initializes {@link SynLookup} fields for an object.
     */
    private void initLookupValuesForObject(BoundObject bObj) throws SynBinderException {
        Map<Class<?>, Collection<Lookup>> clsToLookupsMap = config.getClsToLookupsMap();
        
        Object obj = bObj.getJavaObject();
        Class<?> cls = obj.getClass();
        
        Class<?> curCls = cls;
        while (curCls != null) {
            Collection<Lookup> lookups = clsToLookupsMap.get(curCls);
            for (Lookup lookup : lookups) {
                lookup.bind(bObj, this);
            }
            curCls = curCls.getSuperclass();
        }
    }

    /**
     * Invokes initialization methods.
     */
    private void invokeInitMethods() throws SynBinderException {
        for (BoundObject bObj : bObjs) {
            invokInitMethodsForObject(bObj);
        }
    }

    /**
     * Invokes initialization methods for an object.
     */
    private void invokInitMethodsForObject(BoundObject bObj) throws SynBinderException {
        Map<Class<?>, Collection<InitMethod>> clsToInitMethodsMap = config.getClsToInitMethodsMap();
        
        Object obj = bObj.getJavaObject();
        Class<?> cls = obj.getClass();
        
        Class<?> curCls = cls;
        while (curCls != null) {
            Collection<InitMethod> initMethods = clsToInitMethodsMap.get(curCls);
            if (initMethods != null) {
                for (InitMethod initMethod : initMethods) {
                    initMethod.invoke(obj);
                }
            }
            curCls = curCls.getSuperclass();
        }
    }
}
