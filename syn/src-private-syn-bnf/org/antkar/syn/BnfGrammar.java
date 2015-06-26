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
package org.antkar.syn;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antkar.syn.TokenDescriptor;

/**
 * BNF grammar.
 */
class BnfGrammar {
    private final List<BnfNonterminal> startNonterminals;
    private final List<BnfNonterminal> nonterminals;
    private final List<TokenDescriptor> tokens;
    private final List<BnfElement> elements;

    /**
     * Constructs a BNF grammar.
     * 
     * @param startNonterminals the list of start nonterminals. Must contain at least one element.
     * @param nonterminalsCol the list of all nonterminals used in the grammar.
     * @param terminalsCol the list of all terminals used in the grammar.
     */
    BnfGrammar(
            List<BnfNonterminal> startNonterminals,
            Collection<BnfNonterminal> nonterminalsCol,
            Collection<BnfTerminal> terminalsCol)
    {
        assert startNonterminals != null;
        this.startNonterminals = Collections.unmodifiableList(new ArrayList<>(startNonterminals));
        this.elements = calcElementsList(nonterminalsCol, terminalsCol);
        
        verifyElementIndicies();

        nonterminals = calcNonterminals(nonterminalsCol);
        tokens = calcTokens(terminalsCol);
    }

    /**
     * Returns start nonterminals of this grammar.
     */
    List<BnfNonterminal> getStartNonterminals() {
        return startNonterminals;
    }
    
    /**
     * Returns the list of all nonterminals of this grammar.
     */
    List<BnfNonterminal> getNonterminals() {
        return nonterminals;
    }
    
    /**
     * Returns the list of all tokens defined in this grammar.
     */
    List<TokenDescriptor> getTokens() {
        return tokens;
    }

    /**
     * Returns the list of all grammar elements of this grammar.
     */
    List<BnfElement> getElements() {
        return elements;
    }

    /**
     * Prints this grammar to the specified print stream. For debug purposes.
     */
    void print(PrintStream out) {
        for (BnfNonterminal nt : nonterminals) {
            nt.print(out);
        }
    }

    /**
     * Verifies whether element indices are correct.
     */
    private void verifyElementIndicies() {
        //Begin with start nonterminals, then go through all elements reachable from them.
        //This approach is more reliable than iterating over the collection of nonterminals passed to the
        //constructor, because it will detect a problem if a nonterminal is used in a grammar rule, but
        //is not included into the collection.
        
        Set<BnfNonterminal> ntSet = new HashSet<>(startNonterminals);
        List<BnfNonterminal> ntList = new ArrayList<>(startNonterminals);

        //Go through all nonterminals in the list.
        for (int pos = 0; pos < ntList.size(); ++pos) {
            BnfNonterminal nonterminal = ntList.get(pos);
            //Go through all elements used in this nonterminal's productions.
            for (BnfProduction production : nonterminal.getProductions()) {
                for (BnfElement element : production.getElements()) {
                    //This method may add other nonterminals to the list.
                    verifyElementIndex(ntList, ntSet, element);
                }
            }
        }
    }

    /**
     * Verifies that the index of the specified element is correct. If the element is a nonterminal, it is
     * added to the list of nonterminals.
     */
    private void verifyElementIndex(
            List<BnfNonterminal> ntList,
            Set<BnfNonterminal> ntSet,
            BnfElement element)
    {
        //Verify the index.
        int elIndex = element.getElementIndex();
        assert element == elements.get(elIndex);

        if (element instanceof BnfNonterminal) {
            //Add the nonterminal to the list.
            BnfNonterminal subNonterminal = (BnfNonterminal) element;
            if (!ntSet.contains(subNonterminal)) {
                ntSet.add(subNonterminal);
                ntList.add(subNonterminal);
            }
        }
    }

    /**
     * Creates a combined list of grammar elements from terminal and nonterminal collections.
     * Verifies indices.
     */
    private static List<BnfElement> calcElementsList(
            Collection<BnfNonterminal> nts,
            Collection<BnfTerminal> trs)
    {
        //Create a combined list.
        List<BnfElement> elList = new ArrayList<>(nts.size() + trs.size());
        elList.addAll(nts);
        elList.addAll(trs);
        
        //Sort the list according to elements' indices.
        Collections.sort(elList, ELEMENT_COMPARATOR);
        
        //Check that indices correspond to positions in the list.
        for (int i = 0, n = elList.size(); i < n; ++i) {
            int index = elList.get(i).getElementIndex();
            if (index != i) {
                throw new IllegalStateException("Invalid element index: " + i + ", " + index);
            }
        }
        
        return Collections.unmodifiableList(elList);
    }

    /**
     * Sorts a list of nonterminals and verified their indices.
     */
    private static void sortNonterminals(List<BnfNonterminal> nonterminals) {
        Collections.sort(nonterminals, NONTERMINAL_COMPARATOR);
        for (int i = 0, n = nonterminals.size(); i < n; ++i) {
            int index = nonterminals.get(i).getIndex();
            if (index != i) {
                throw new IllegalStateException("Invalid nonterminal index: " + i + ", " + index);
            }
        }
    }

    /**
     * Creates an ordered list of nonterminals. Verifies indices.
     */
    private static List<BnfNonterminal> calcNonterminals(Collection<BnfNonterminal> nonterminalsCol) {
        List<BnfNonterminal> tmpNonterminals = new ArrayList<>(nonterminalsCol);
        sortNonterminals(tmpNonterminals);
        return Collections.unmodifiableList(tmpNonterminals);
    }

    /**
     * Creates an ordered list of tokens.
     */
    private static List<TokenDescriptor> calcTokens(Collection<BnfTerminal> terminals) {
        List<BnfTerminal> tmpTerminals = new ArrayList<>(terminals);
        
        //Sort terminals by indices in order to make the behavior definite.
        Collections.sort(tmpTerminals, ELEMENT_COMPARATOR);
        
        //Created list of token descriptors.
        List<TokenDescriptor> tokens = new ArrayList<>();
        for (BnfTerminal terminal : tmpTerminals) {
            TokenDescriptor tokenDescriptor = terminal.getTokenDescriptor();
            tokens.add(tokenDescriptor);
        }
        
        List<TokenDescriptor> result = Collections.unmodifiableList(tokens);
        return result;
    }

    /**
     * Element comparator. Compares grammar elements by their element indices.
     */
    private static final Comparator<BnfElement> ELEMENT_COMPARATOR = 
        new Comparator<BnfElement>()
    {
        @Override
        public int compare(BnfElement o1, BnfElement o2) {
            return o1.getElementIndex() - o2.getElementIndex();
        }
    };

    /**
     * Nonterminal comparator. Compares nonterminal indices.
     */
    private static final Comparator<BnfNonterminal> NONTERMINAL_COMPARATOR = 
        new Comparator<BnfNonterminal>()
    {
        @Override
        public int compare(BnfNonterminal o1, BnfNonterminal o2) {
            return o1.getIndex() - o2.getIndex();
        }
    };
}
