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
package org.antkar.syn.internal.lrtables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.bnf.BnfElement;
import org.antkar.syn.internal.bnf.BnfGrammar;
import org.antkar.syn.internal.bnf.BnfNonterminal;
import org.antkar.syn.internal.bnf.BnfProduction;
import org.antkar.syn.internal.bnf.BnfTerminal;
import org.antkar.syn.internal.parser.IParserAction;
import org.antkar.syn.internal.parser.IParserGetter;
import org.antkar.syn.internal.parser.ParserResultAction;
import org.antkar.syn.internal.parser.ParserStackGetter;

/**
 * LR parser configuration builder.
 */
public final class ParserConfigurator {

    private final IndexMap<BnfNonterminal, ParserItem[]> directItemsMap;
    private final IndexMap<BnfNonterminal, ParserItem[]> indirectItemsMap;

    private final IndexMap<BnfNonterminal, ParserNonterminal> nonterminalMap;
    private final IndexMap<BnfProduction, ParserProduction> productionMap;

    private final IndexSet<ParserItem> tempIndirectItemSet;
    private final IndexSet<ParserItem> tempClosureItemSet;
    private final IndexSet<BnfNonterminal> tempIndirectNonterminalSet;
    private final IndexMap<BnfElement, List<ParserItem>> tempTransitionsMap;

    private final Map<ParserItemSet, ParserState> stateMap = new HashMap<>();
    private final List<ParserTempState> stateList = new ArrayList<>();
    private final Map<String, ParserState> startStateMap = new HashMap<>();
    private final List<TokenDescriptor> tokenDescriptors;

    private final Counter nonterminalCounter = new Counter();
    private final Counter itemCounter = new Counter();

    private ParserConfigurator(BnfGrammar grammar) {
        int nNonterminals = grammar.getNonterminals().size();
        directItemsMap = new IndexMap<>(NONTERMINAL_INDEX_PROVIDER, nNonterminals);
        indirectItemsMap = new IndexMap<>(NONTERMINAL_INDEX_PROVIDER, nNonterminals);
        nonterminalMap = new IndexMap<>(NONTERMINAL_INDEX_PROVIDER, nNonterminals);
        tempIndirectNonterminalSet = new IndexSet<>(NONTERMINAL_INDEX_PROVIDER, nNonterminals);

        int nProductions = calcNProductions(grammar);
        productionMap = new IndexMap<>(PRODUCTION_INDEX_PROVIDER, nProductions);

        int nItems = calcNItems(grammar);
        tempIndirectItemSet = new IndexSet<>(ITEM_INDEX_PROVIDER, nItems);
        tempClosureItemSet = new IndexSet<>(ITEM_INDEX_PROVIDER, nItems);

        int nElements = grammar.getElements().size();
        tempTransitionsMap = new IndexMap<>(ELEMENT_INDEX_PROVIDER, nElements);

        tokenDescriptors = grammar.getTokens();
    }

    /**
     * Creates a parser configuration.
     */
    public static ParserConfiguration makeConfiguration(BnfGrammar grammar) {
        ParserConfigurator configurator = new ParserConfigurator(grammar);
        for (BnfNonterminal startNonterminal : grammar.getStartNonterminals()) {
            configurator.addStartNonterminal(startNonterminal);
        }
        ParserConfiguration result = configurator.createConfiguration();
        return result;
    }

    /**
     * Adds a start nonterminal to the configuration, generates all derived LR states.
     */
    private void addStartNonterminal(BnfNonterminal nonterminal) {
        assert nonterminal != null;
        assert !startStateMap.containsKey(nonterminal.getName());

        List<ParserItem> itemList = new ArrayList<>();
        ParserItem extendedItem = calcExtendedItem(nonterminal);
        itemList.add(extendedItem);
        ParserItem[] startItems = calcClosure(itemList);

        int start = stateList.size();
        ParserState state = addParserState(startItems);
        startStateMap.put(nonterminal.getName(), state);

        for (int pos = start; pos < stateList.size(); ++pos) {
            ParserTempState tState = stateList.get(pos);
            processNewState(tState);
        }
    }

    /**
     * Creates all LR states that are directly reachable from the given LR state.
     */
    private void processNewState(ParserTempState tState) {
        fillTransitionsForState(tState);

        List<ParserGoto> pGotos = new ArrayList<>();
        List<ParserShift> pShifts = new ArrayList<>();
        processTransitions(pGotos, pShifts);

        tState.getState().setTransitions(pShifts, pGotos);
    }

    /**
     * Collects transitions for the given LR state.
     */
    private void fillTransitionsForState(ParserTempState tState) {
        tempTransitionsMap.clear();

        for (ParserItem item : tState.getItemSet().getItems()) {
            BnfElement element = item.getElement();
            if (element != null) {
                ParserItem nextItem = item.getNext();
                addToListMap(tempTransitionsMap, element, nextItem);
            }
        }
    }

    /**
     * Creates GOTO and SHIFT transitions for an LR state.
     */
    private void processTransitions(List<ParserGoto> pGotoList, List<ParserShift> pShiftList) {
        for (int pos = 0, size = tempTransitionsMap.size(); pos < size; ++pos) {
            BnfElement element = tempTransitionsMap.getKeyAt(pos);
            List<ParserItem> itemList = tempTransitionsMap.getValueAt(pos);
            processTransition(pGotoList, pShiftList, element, itemList);
        }
    }

    /**
     * Creates a GOTO or SHIFT transition for the given BNF element.
     */
    private void processTransition(
            List<ParserGoto> pGotoList,
            List<ParserShift> pShiftList,
            BnfElement element,
            List<ParserItem> itemList)
    {
        ParserItem[] items = calcClosure(itemList);
        ParserState state = addParserState(items);

        if (element instanceof BnfNonterminal) {
            //The element is nonterminal - GOTO.
            BnfNonterminal bNonterminal = (BnfNonterminal) element;
            ParserNonterminal pNonterminal = getParserNonterminal(bNonterminal);
            ParserGoto pGoto = new ParserGoto(pNonterminal, state);
            pGotoList.add(pGoto);
        } else {
            //Terminal - SHIFT.
            BnfTerminal bTerminal = (BnfTerminal) element;
            TokenDescriptor tokenDescriptor = bTerminal.getTokenDescriptor();
            ParserShift shift = new ParserShift(tokenDescriptor, state);
            pShiftList.add(shift);
        }
    }

    /**
     * Finds an existing or creates a new LR state based on the given set of LR items.
     */
    private ParserState addParserState(ParserItem[] items) {
        ParserItemSet iSet = new ParserItemSet(items);
        ParserState state = stateMap.get(iSet);

        if (state == null) {
            state = new ParserState(stateMap.size(), calcReduceProductions(items));
            ParserTempState tState = new ParserTempState(iSet, state);
            stateMap.put(iSet, state);
            stateList.add(tState);
        }

        return state;
    }

    /**
     * Creates a {@link ParserConfiguration} object.
     */
    private ParserConfiguration createConfiguration() {
        List<ParserState> pStateList = new ArrayList<>();

        for (ParserTempState tState : stateList) {
            pStateList.add(tState.getState());
        }

        ParserConfiguration result = new ParserConfiguration(startStateMap, pStateList, tokenDescriptors);
        return result;
    }

    /**
     * Returns direct LR items for the given nonterminal.
     */
    private ParserItem[] getDirectItems(BnfNonterminal nonterminal) {
        ParserItem[] result = directItemsMap.get(nonterminal);

        if (result == null) {
            result = calcDirectItems(nonterminal);
            directItemsMap.put(nonterminal, result);
        }

        return result;
    }

    /**
     * Calculates a closure of LR items.
     */
    private ParserItem[] calcClosure(List<ParserItem> sourceItems) {
        tempClosureItemSet.clear();

        //A closure is a union of indirect items of all nonterminals pointed by source items.
        for (ParserItem item : sourceItems) {
            tempClosureItemSet.add(item);
            BnfElement element = item.getElement();
            if (element instanceof BnfNonterminal) {
                BnfNonterminal nonterminal = (BnfNonterminal) element;
                ParserItem[] indirectItems = getIndirectItems(nonterminal);
                for (ParserItem indirectItem : indirectItems) {
                    tempClosureItemSet.add(indirectItem);
                }
            }
        }

        ParserItem[] result = tempClosureItemSet.asArray(ParserItem.class);
        return result;
    }

    /**
     * Returns indirect LR items for the given nonterminal.
     */
    private ParserItem[] getIndirectItems(BnfNonterminal nonterminal) {
        ParserItem[] result = indirectItemsMap.get(nonterminal);

        if (result == null) {
            result = calcIndirectItems(nonterminal);
            indirectItemsMap.put(nonterminal, result);
        }

        return result;
    }

    /**
     * Calculates indirect LR items for a nonterminal.
     */
    private ParserItem[] calcIndirectItems(BnfNonterminal nonterminal) {
        tempIndirectItemSet.clear();

        BnfNonterminal[] indirectNonterminals = getIndirectNonterminals(nonterminal);
        for (int i = 0, n = indirectNonterminals.length; i < n; ++i) {
            ParserItem[] directItems = getDirectItems(indirectNonterminals[i]);
            for (ParserItem directItem : directItems) {
                tempIndirectItemSet.add(directItem);
            }
        }

        ParserItem[] result = tempIndirectItemSet.asArray(ParserItem.class);
        return result;
    }

    /**
     * Returns indirect nonterminals for the given nonterminal.
     */
    private BnfNonterminal[] getIndirectNonterminals(BnfNonterminal nonterminal) {
        tempIndirectNonterminalSet.clear();
        tempIndirectNonterminalSet.add(nonterminal);

        for (int pos = 0; pos < tempIndirectNonterminalSet.size(); ++pos) {
            BnfNonterminal curNonterminal = tempIndirectNonterminalSet.getAt(pos);
            for (ParserItem item : getDirectItems(curNonterminal)) {
                BnfElement element = item.getElement();
                if (element instanceof BnfNonterminal) {
                    BnfNonterminal subNonterminal = (BnfNonterminal) element;
                    tempIndirectNonterminalSet.add(subNonterminal);
                }
            }
        }

        BnfNonterminal[] result = tempIndirectNonterminalSet.asArray(BnfNonterminal.class);
        return result;
    }

    /**
     * Creates an extended start nonterminal from the given nonterminal.
     */
    private ParserNonterminal createExtendedNonterminal(BnfNonterminal bNonterminal) {
        int index = nonterminalCounter.next();
        String name = "@" + bNonterminal.getName();
        ParserNonterminal result = new ParserNonterminal(index, name, true);
        return result;
    }

    /**
     * Returns the parser nonterminal descriptor for the given BNF nonterminal.
     */
    private ParserNonterminal getParserNonterminal(BnfNonterminal bNonterminal) {
        ParserNonterminal result = nonterminalMap.get(bNonterminal);

        if (result == null) {
            int index = nonterminalCounter.next();
            result = new ParserNonterminal(index, bNonterminal.getName(), false);
            nonterminalMap.put(bNonterminal, result);
        }

        return result;
    }

    /**
     * Returns the parser production descriptor for the given BNF production.
     */
    private ParserProduction getParserProduction(BnfProduction bProduction) {
        ParserProduction result = productionMap.get(bProduction);

        if (result == null) {
            ParserNonterminal pNonterminal = getParserNonterminal(bProduction.getNonterminal());
            result = new ParserProduction(pNonterminal, bProduction.getElements().size(),
                    bProduction.getParserAction());
            productionMap.put(bProduction, result);
        }

        return result;
    }

    /**
     * Calculates direct LR items for the given BNF nonterminal.
     */
    private ParserItem[] calcDirectItems(BnfNonterminal nonterminal) {
        ParserItem[] result = new ParserItem[nonterminal.getProductions().size()];
        for (int i = 0, n = nonterminal.getProductions().size(); i < n; ++i) {
            BnfProduction production = nonterminal.getProductions().get(i);
            result[i] = calcDirectItemForProduction(production);
        }
        return result;
    }

    /**
     * Creates an extended BNF nonterminal for the given nonterminal and returns the start LR
     * item of the extended nonterminal's production.
     */
    private ParserItem calcExtendedItem(BnfNonterminal bNonterminal) {
        ParserNonterminal pNonterminal = createExtendedNonterminal(bNonterminal);
        IParserGetter getter = new ParserStackGetter(0);
        IParserAction action = new ParserResultAction(getter);

        ParserProduction production = new ParserProduction(pNonterminal, 1, action);
        ParserItem endItem = new ParserItem(itemCounter.next(), 1, null, production, null);
        ParserItem startItem = new ParserItem(itemCounter.next(), 0, endItem, production, bNonterminal);

        return startItem;
    }

    /**
     * Creates LR items for each position in the given production, returns the first LR item.
     */
    private ParserItem calcDirectItemForProduction(BnfProduction bProduction) {
        ParserItem next = null;
        ParserProduction pProduction = getParserProduction(bProduction);

        for (int n = bProduction.getElements().size(), i = n; i >= 0; --i) {
            int index = itemCounter.next();
            BnfElement element = null;
            if (i < n) {
                element = bProduction.getElements().get(i);
            }
            ParserItem cur = new ParserItem(index, i, next, pProduction, element);
            next = cur;
        }

        return next;
    }

    /**
     * Returns the list of parser productions that can be reduced in the given set of
     * LR items.
     */
    private static List<ParserProduction> calcReduceProductions(ParserItem[] items) {
        List<ParserProduction> list = new ArrayList<>();

        for (ParserItem item : items) {
            if (item.getNext() == null) {
                ParserProduction pProduction = item.getProduction();
                list.add(pProduction);
            }
        }

        return list;
    }

    /**
     * Calculates the maximum number of LR items defined in the grammar. Used to initialize
     * an indexed set/map.
     */
    private static int calcNItems(BnfGrammar grammar) {
        int result = 0;

        for (BnfNonterminal nonterminal : grammar.getNonterminals()) {
            for (BnfProduction production : nonterminal.getProductions()) {
                result += production.getElements().size() + 1;
            }
        }

        //Additional item for each start nonterminal (a production containing a single element is
        //created for each start nonterminal).
        result += grammar.getStartNonterminals().size() * 2;

        return result;
    }

    /**
     * Calculates the number of BNF productions defined in the grammar. Used to initialize
     * an indexed set/map.
     */
    private static int calcNProductions(BnfGrammar grammar) {
        int result = 0;
        for (BnfNonterminal nonterminal : grammar.getNonterminals()) {
            result += nonterminal.getProductions().size();
        }
        return result;
    }

    /**
     * Adds an element to an indexed map of lists.
     */
    private static <K, V> void addToListMap(IndexMap<K, List<V>> map, K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        list.add(value);
    }

    /**
     * BNF element index provider.
     */
    private static final IIndexProvider<BnfElement> ELEMENT_INDEX_PROVIDER = new IIndexProvider<BnfElement>()
    {
        @Override
        public int getIndex(BnfElement t) {
            return t.getElementIndex();
        }
    };

    /**
     * BNF nonterminal index provider.
     */
    private static final IIndexProvider<BnfNonterminal> NONTERMINAL_INDEX_PROVIDER =
            new IIndexProvider<BnfNonterminal>()
    {
        @Override
        public int getIndex(BnfNonterminal t) {
            return t.getIndex();
        }
    };

    /**
     * LR item index provider.
     */
    private static final IIndexProvider<ParserItem> ITEM_INDEX_PROVIDER = new IIndexProvider<ParserItem>()
    {
        @Override
        public int getIndex(ParserItem t) {
            return t.getIndex();
        }
    };

    /**
     * BNF production index provider.
     */
    private static final IIndexProvider<BnfProduction> PRODUCTION_INDEX_PROVIDER =
            new IIndexProvider<BnfProduction>()
    {
        @Override
        public int getIndex(BnfProduction t) {
            return t.getIndex();
        }
    };
}
