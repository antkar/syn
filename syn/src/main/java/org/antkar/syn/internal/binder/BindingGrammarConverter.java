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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.ValueNode;
import org.antkar.syn.binder.SynBinderException;
import org.antkar.syn.internal.StringValueNode;
import org.antkar.syn.internal.ebnf.EbnfElement;
import org.antkar.syn.internal.ebnf.EbnfElementProcessor;
import org.antkar.syn.internal.ebnf.EbnfGrammar;
import org.antkar.syn.internal.ebnf.EbnfNestedElement;
import org.antkar.syn.internal.ebnf.EbnfNonterminal;
import org.antkar.syn.internal.ebnf.EbnfNonterminalElement;
import org.antkar.syn.internal.ebnf.EbnfOptionalElement;
import org.antkar.syn.internal.ebnf.EbnfProduction;
import org.antkar.syn.internal.ebnf.EbnfProductions;
import org.antkar.syn.internal.ebnf.EbnfRepetitionElement;
import org.antkar.syn.internal.ebnf.EbnfTerminalElement;
import org.antkar.syn.internal.ebnf.EbnfValueElement;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;

/**
 * This class converts an arbitrary EBNF grammar to an extended Binder EBNF grammar. The extended
 * grammar has the same syntax as the original one, but it has additional constant attributes which
 * specify how to construct Java objects from an Abstract Syntax Tree produced by that grammar.
 */
final class BindingGrammarConverter {

    private final Map<String, EbnfNonterminal> genNtsMap = new HashMap<>();
    private final Map<String, BindingNonterminal> bindingNtsMap = new HashMap<>();

    private EbnfGrammar genGrammar;

    private BindingGrammarConverter(){}

    /**
     * Returns the generated extended grammar.
     */
    EbnfGrammar getGenGrammar() {
        return genGrammar;
    }

    /**
     * Returns the map of binding nonterminals.
     */
    Map<String, BindingNonterminal> getBindingNtsMap() {
        return bindingNtsMap;
    }

    /**
     * Converts a grammar.
     */
    static BindingGrammarConverter convertBindingGrammar(
            String startNtName,
            EbnfGrammar orgGrammar) throws SynException
    {
        BindingGrammarConverter converter = new BindingGrammarConverter();
        converter.convert(startNtName, orgGrammar);
        return converter;
    }

    /**
     * Converts a grammar. Implementation.
     */
    private void convert(String startNtName, EbnfGrammar orgGrammar) throws SynException {
        EbnfNonterminal orgStartNt = findStartNonterminal(orgGrammar, startNtName);
        EbnfNonterminal genStartNt = processNonterminal(orgStartNt);
        genGrammar = createGenGrammar(orgGrammar, genStartNt);
    }

    /**
     * Converts an EBNF nonterminal.
     */
    private EbnfNonterminal processNonterminal(EbnfNonterminal orgNt) throws SynException {
        String ntName = orgNt.getName();
        EbnfNonterminal genNt = genNtsMap.get(ntName);
        if (genNt == null) {
            genNt = new EbnfNonterminal(ntName);
            genNtsMap.put(ntName, genNt);
            defineNonterminal(orgNt, genNt);
        }
        return genNt;
    }

    /**
     * Defines a new EBNF nonterminal.
     */
    private void defineNonterminal(EbnfNonterminal orgNt, EbnfNonterminal genNt) throws SynException {
        Map<String, EbnfProduction> innerPrs = new HashMap<>();
        List<EbnfNonterminal> innerNts = new ArrayList<>();

        EbnfProductions orgProductions = orgNt.getProductions();
        EbnfProductions genProductions = convertTopProductions(orgProductions, innerPrs, innerNts);
        genNt.setProductions(genProductions);

        String ntName = orgNt.getName();
        BindingNonterminal bindingNt = new BindingNonterminal(ntName, innerPrs, innerNts);
        bindingNtsMap.put(ntName, bindingNt);
    }

    /**
     * Converts top EBNF productions. Top productions are not nested into other productions.
     */
    private EbnfProductions convertTopProductions(
            EbnfProductions orgProductions,
            Map<String, EbnfProduction> innerPrs,
            List<EbnfNonterminal> innerNts) throws SynException
    {
        List<EbnfProduction> orgProductionsList = orgProductions.asList();
        List<EbnfProduction> genProductionsList = new ArrayList<>();

        for (EbnfProduction orgProduction : orgProductionsList) {
            EbnfProduction genProduction = convertTopProduction(orgProduction, innerPrs, innerNts);
            genProductionsList.add(genProduction);
        }

        EbnfProductions genProductions = new EbnfProductions(genProductionsList);
        return genProductions;
    }

    /**
     * Converts a top EBNF production.
     */
    private EbnfProduction convertTopProduction(
            EbnfProduction orgProduction,
            Map<String, EbnfProduction> innerPrs,
            List<EbnfNonterminal> innerNts) throws SynException
    {
        //Convert production's syntax.
        List<EbnfElement> orgElements = orgProduction.getElements();
        List<EbnfElement> genElements = new ArrayList<>();
        convertProduction(genElements, orgElements, false);

        //Add semantics information used by Binder.
        defineTopProductionResult(orgProduction, orgElements, genElements, innerPrs, innerNts);

        EbnfProduction genProduction = new EbnfProduction(genElements);
        return genProduction;
    }

    /**
     * Adds a semantic information to an EBNF production.
     */
    private void defineTopProductionResult(
            EbnfProduction orgProduction,
            List<EbnfElement> orgElements,
            List<EbnfElement> genElements,
            Map<String, EbnfProduction> innerPrs,
            List<EbnfNonterminal> innerNts)
    {
        EbnfNonterminalElement orgNtElement = getSingleNonterminalElement(orgElements);
        if (orgNtElement != null) {
            //The production consists of only one nonterminal, or a "result" attribute is specified.
            //In this case the production will not produce a new value, it will pass through the value
            //returned by its element.
            EbnfNonterminal orgNt = orgNtElement.getNonterminal();
            innerNts.add(orgNt);
        } else {
            //Otherwise, a production key constant is added to the production.

            //Create a production key.
            String ntName = orgProduction.getNonterminal().getName();
            String productionKey = String.format("%s_%s", ntName, innerPrs.size());
            innerPrs.put(productionKey, orgProduction);

            //Add the key to the production.
            ValueNode genClsNameNode = new StringValueNode(null, productionKey);
            EbnfElement genClsNameElement =
                    new EbnfValueElement(BinderEngine.PRODUCTION_ATTR, null, genClsNameNode);
            genElements.add(genClsNameElement);
        }
    }

    /**
     * Returns an EBNF nonterminal element either if the specified list contains only that element, or
     * if it contains a nonterminal element with a special "result" attribute. Otherwise, returns
     * <code>null</code>.
     */
    private static EbnfNonterminalElement getSingleNonterminalElement(List<EbnfElement> orgElements) {
        //Check result key.
        for (EbnfElement orgElement : orgElements) {
            String key = orgElement.getAttribute();
            if (EbnfToBnfConverter.RESULT_KEY.equals(key) && orgElement instanceof EbnfNonterminalElement) {
                return (EbnfNonterminalElement)orgElement;
            }
        }

        //Check single nonterminal.
        if (orgElements.size() == 1) {
            EbnfElement orgElement = orgElements.get(0);
            if (orgElement.getAttribute() == null && orgElement instanceof EbnfNonterminalElement) {
                return (EbnfNonterminalElement)orgElement;
            }
        }

        return null;
    }

    /**
     * Converts an EBNF production.
     */
    private void convertProduction(
            List<EbnfElement> genElements,
            List<EbnfElement> orgElements,
            boolean isNestedProduction) throws SynException
    {
        for (EbnfElement orgElement : orgElements) {
            EbnfElement genElement = convertElement(orgElement, isNestedProduction);
            genElements.add(genElement);
        }
    }

    /**
     * Converts an EBNF element.
     */
    private EbnfElement convertElement(
            EbnfElement orgElement0,
            boolean isNestedElement) throws SynException
    {
        checkElementAttribute(orgElement0, isNestedElement);

        EbnfElement genElement = orgElement0.invokeProcessor(new EbnfElementProcessor<EbnfElement>() {
            @Override
            public EbnfElement processValueElement(EbnfValueElement orgElement) {
                return orgElement;
            }

            @Override
            public EbnfElement processTerminalElement(EbnfTerminalElement orgElement) {
                return orgElement;
            }

            @Override
            public EbnfElement processRepetitionElement(EbnfRepetitionElement orgElement)
                    throws SynException
            {
                return convertRepetitionElement(orgElement);
            }

            @Override
            public EbnfElement processNonterminalElement(EbnfNonterminalElement orgElement)
                    throws SynException
            {
                return convertNonterminalElement(orgElement);
            }

            @Override
            public EbnfElement processNestedElement(EbnfNestedElement orgElement) throws SynException {
                return convertNestedElement(orgElement);
            }

            @Override
            public EbnfElement processOptionalElement(EbnfOptionalElement orgElement) throws SynException {
                return convertOptionalElement(orgElement);
            }
        });

        return genElement;
    }

    /**
     * Converts a repetition element.
     */
    private EbnfElement convertRepetitionElement(EbnfRepetitionElement orgElement) throws SynException {
        EbnfProductions orgBodyProductions = orgElement.getBody();
        EbnfProductions genBodyProductions = convertNestedProductions(orgBodyProductions);

        EbnfProductions orgSeparatorProductions = orgElement.getSeparator();
        EbnfProductions genSeparatorProductions = orgSeparatorProductions == null ? null
                : convertNestedProductions(orgSeparatorProductions);

        String key = orgElement.getAttribute();
        TextPos keyPos = orgElement.getAttributePos();
        boolean nullable = orgElement.isNullable();

        return new EbnfRepetitionElement(key, keyPos, genBodyProductions, genSeparatorProductions, nullable);
    }

    /**
     * Converts a nonterminal element.
     */
    private EbnfElement convertNonterminalElement(EbnfNonterminalElement orgElement) throws SynException {
        EbnfNonterminal orgNt = orgElement.getNonterminal();
        EbnfNonterminal genNt = processNonterminal(orgNt);

        String key = orgElement.getAttribute();
        TextPos keyPos = orgElement.getAttributePos();
        return new EbnfNonterminalElement(key, keyPos, genNt);
    }

    /**
     * Converts a nested element.
     */
    private EbnfElement convertNestedElement(EbnfNestedElement orgElement) throws SynException {
        EbnfProductions orgBodyProductions = orgElement.getBody();
        EbnfProductions genBodyProductions = convertNestedProductions(orgBodyProductions);

        String key = orgElement.getAttribute();
        TextPos keyPos = orgElement.getAttributePos();
        return new EbnfNestedElement(key, keyPos, genBodyProductions);
    }

    /**
     * Converts an optional element.
     */
    private EbnfElement convertOptionalElement(EbnfOptionalElement orgElement) throws SynException {
        EbnfProductions orgBodyProductions = orgElement.getBody();
        EbnfProductions genBodyProductions = convertNestedProductions(orgBodyProductions);

        String key = orgElement.getAttribute();
        TextPos keyPos = orgElement.getAttributePos();
        return new EbnfOptionalElement(key, keyPos, genBodyProductions);
    }

    /**
     * Converts a set of nested EBNF productions.
     */
    private EbnfProductions convertNestedProductions(EbnfProductions orgProductions) throws SynException {
        List<EbnfProduction> orgProductionsList = orgProductions.asList();
        List<EbnfProduction> genProductionsList = new ArrayList<>();

        for (EbnfProduction orgProduction : orgProductionsList) {
            EbnfProduction genProduction = convertNestedProduction(orgProduction);
            genProductionsList.add(genProduction);
        }

        EbnfProductions genProductions = new EbnfProductions(genProductionsList);
        return genProductions;
    }

    /**
     * Converts a nested EBNF production.
     */
    private EbnfProduction convertNestedProduction(EbnfProduction orgProduction) throws SynException {
        List<EbnfElement> orgElements = orgProduction.getElements();
        List<EbnfElement> genElements = new ArrayList<>();

        convertProduction(genElements, orgElements, true);

        EbnfProduction genProduction = new EbnfProduction(genElements);
        return genProduction;
    }

    /**
     * Checks if the attribute of the given EBNF element violates the Binder limitations.
     */
    private static void checkElementAttribute(
            EbnfElement orgElement,
            boolean isNestedElement) throws SynBinderException
    {
        String key = orgElement.getAttribute();
        if (BinderEngine.PRODUCTION_ATTR.equals(key)
                || (isNestedElement && EbnfToBnfConverter.RESULT_KEY.equals(key)))
        {
            throw new SynBinderException(String.format(
                    "Special name '%s' cannot be used as an attribute", key));
        }
    }

    /**
     * Finds a start EBNF nonterminal by name. Throws an exception if there is no such start nonterminal.
     */
    private static EbnfNonterminal findStartNonterminal(EbnfGrammar eGrammar, String startNtName)
            throws SynBinderException
    {
        for (EbnfNonterminal orgNt : eGrammar.getStartNonterminals()) {
            if (startNtName.equals(orgNt.getName())) {
                return orgNt;
            }
        }

        throw new SynBinderException(
                String.format("There is no nonterminal %s or it is not a start nonterminal", startNtName));
    }

    /**
     * Creates an EBNF grammar object.
     */
    private static EbnfGrammar createGenGrammar(EbnfGrammar orgGrammar, EbnfNonterminal genStartNt) {
        List<EbnfNonterminal> genStartNts = Collections.singletonList(genStartNt);
        List<EbnfTerminalElement> terminals = orgGrammar.getTerminals();
        return new EbnfGrammar(genStartNts, terminals);
    }
}
