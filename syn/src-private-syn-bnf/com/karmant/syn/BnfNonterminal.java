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
import java.util.List;

/**
 * BNF nonterminal element.
 */
class BnfNonterminal extends BnfElement {
    private final int index;
    private final String name;
    private List<BnfProduction> productions;

    /**
     * Creates a nonterminal, leaving the productions uninitialized. Productions must be set by
     * {@link #setProductions(List)} method later, since dependencies between nonterminals can be cyclic.
     */
    BnfNonterminal(int elementIndex, int index, String name) {
        super(elementIndex);
        assert index >= 0;
        assert name != null;
        assert name.length() > 0;
        this.index = index;
        this.name = name;
        
        productions = null;
    }
    
    /**
     * Sets productions of this nonterminal. Must be called one and only one time.
     */
    void setProductions(List<BnfProduction> productions) {
        assert productions != null;
        assert productions.size() > 0;
        assert this.productions == null;
        
        this.productions = Collections.unmodifiableList(new ArrayList<>(productions));
        for (BnfProduction production : this.productions) {
            production.setNonterminal(this);
        }
    }
    
    /**
     * Returns the index of this nonterminal in the grammar.
     */
    int getIndex() {
        return index;
    }
    
    /**
     * Returns the list of productions. Productions must have been set before.
     */
    List<BnfProduction> getProductions() {
        assert productions != null;
        return productions;
    }
    
    /**
     * Returns the name of this nonterminal.
     */
    String getName() {
        return name;
    }
    
    /**
     * Prints the definition of this nonterminal to the specified print stream. Used for debug purposes.
     */
    void print(PrintStream out) {
        out.println(name);
        String sep = ":";
        for (BnfProduction production : productions) {
            String s = production.toString();
            out.printf("\t%s %s%n", sep, s);
            sep = "|";
        }
        out.println("\t;");
    }
    
    @Override
    public String toString() {
        return name;
    }
}
