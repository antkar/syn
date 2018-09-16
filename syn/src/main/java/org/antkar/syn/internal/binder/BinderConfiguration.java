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
package org.antkar.syn.internal.binder;

import java.util.Collection;
import java.util.Map;

import org.antkar.syn.internal.binder.lookup.Lookup;

/**
 * Binder configuration. Defines which grammar attributes have to be bound to which Java fields, and how.
 *
 * @param <T> the type of the start nonterminal.
 */
public final class BinderConfiguration<T> {

    private Class<T> classToBind;
    private Map<String, ObjectBinder> prKeyToBinderMap;
    private Map<Class<?>, Collection<Lookup>> clsToLookupsMap;
    private Map<Class<?>, Collection<InitMethod>> clsToInitMethodsMap;

    BinderConfiguration(
            Class<T> classToBind,
            Map<String, ObjectBinder> prKeyToBinderMap,
            Map<Class<?>, Collection<Lookup>> clsToLookupsMap,
            Map<Class<?>, Collection<InitMethod>> clsToInitMethodsMap)
    {
        this.classToBind = classToBind;
        this.prKeyToBinderMap = prKeyToBinderMap;
        this.clsToLookupsMap = clsToLookupsMap;
        this.clsToInitMethodsMap = clsToInitMethodsMap;
    }

    public Class<T> getClassToBind() {
        return classToBind;
    }

    public Map<String, ObjectBinder> getPrKeyToBinderMap() {
        return prKeyToBinderMap;
    }

    public Map<Class<?>, Collection<Lookup>> getClsToLookupsMap() {
        return clsToLookupsMap;
    }

    public Map<Class<?>, Collection<InitMethod>> getClsToInitMethodsMap() {
        return clsToInitMethodsMap;
    }
}
