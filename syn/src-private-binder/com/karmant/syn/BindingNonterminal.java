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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Describes binding properties of a nonterminal.
 */
class BindingNonterminal {

    private final String name;
    private final Map<String, EbnfProduction> innerPrs;
    private final List<EbnfNonterminal> innerNts;
    
    BindingNonterminal(
            String name,
            Map<String, EbnfProduction> innerPrs,
            List<EbnfNonterminal> innerNts)
    {
        this.name = name;
        this.innerPrs = Collections.unmodifiableMap(innerPrs);
        this.innerNts = Collections.unmodifiableList(innerNts);
    }

    /**
     * Returns the map of inner productions of the nonterminal. Keys in the map are production keys.
     */
    Map<String, EbnfProduction> getInnerPrs() {
        return innerPrs;
    }

    /**
     * Returns the list of inner nonterminals.
     */
    List<EbnfNonterminal> getInnerNts() {
        return innerNts;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
