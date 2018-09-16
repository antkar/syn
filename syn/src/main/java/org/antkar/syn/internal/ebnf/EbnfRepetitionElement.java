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
package org.antkar.syn.internal.ebnf;

import java.util.ArrayList;
import java.util.List;

import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.internal.bnf.BnfElement;
import org.antkar.syn.internal.bnf.BnfNonterminal;
import org.antkar.syn.internal.bnf.BnfProduction;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;
import org.antkar.syn.internal.parser.IParserGetter;
import org.antkar.syn.internal.parser.ParserEmptyArrayAction;
import org.antkar.syn.internal.parser.ParserFirstArrayAction;
import org.antkar.syn.internal.parser.ParserNextArrayAction;
import org.antkar.syn.internal.parser.ParserResultAction;
import org.antkar.syn.internal.parser.ParserStackGetter;

/**
 * Repetition EBNF element.
 */
public final class EbnfRepetitionElement extends EbnfCompoundElement {
    private final EbnfProductions separator;
    private final boolean nullable;

    public EbnfRepetitionElement(
            String key,
            TextPos keyPos,
            EbnfProductions body,
            EbnfProductions separator,
            boolean nullable)
    {
        super(key, keyPos, body);
        this.separator = separator;
        this.nullable = nullable;
    }

    /**
     * Returns separator productions. Can be <code>null</code>.
     */
    public EbnfProductions getSeparator() {
        return separator;
    }

    /**
     * Returns <code>true</code> if this repetition element accepts empty sequences, i. e. if it is a
     * zero-many element (but not a one-many).
     */
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException {
        //Create a BNF element equivalent for a one-many repetition element.
        BnfElement bElement = convertOneMany(converter, currentNt);

        if (nullable) {
            //This is a zero-many repetition element. Add an empty production.

            IParserGetter getter = new ParserStackGetter(0);

            BnfProduction bRepetitionProduction = converter.createProduction(
                    new ParserResultAction(getter), bElement);
            BnfProduction bEmptyProduction = converter.createProduction(ParserEmptyArrayAction.INSTANCE);

            List<BnfProduction> bProductions = new ArrayList<>();
            bProductions.add(bRepetitionProduction);
            bProductions.add(bEmptyProduction);

            bElement = converter.createAnonymousNonterminal(currentNt, bProductions);
        }

        return bElement;
    }

    /**
     * Creates a BNF element which is equivalent to a one-many repetition element.
     */
    private BnfElement convertOneMany(EbnfToBnfConverter converter, String currentNt) throws SynException {
        BnfNonterminal bNonterminal = converter.createAnonymousNonterminal(currentNt);

        //Convert body productions into an anonymous BNF nonterminal.
        BnfNonterminal bBodyNonterminal = converter.convertProductionsToNonterminal(
                currentNt, getBody().asList(), false);

        //Create a terminal (i. e. not recursive) production.
        BnfProduction bTerminalProduction = converter.createProduction(
                ParserFirstArrayAction.INSTANCE, bBodyNonterminal);

        BnfProduction bRecursiveProduction;
        if (separator != null) {
            //Separator is specified. Convert separator productions to a nonterminal.
            BnfNonterminal bSeparatorNonterminal =
                converter.convertProductionsToNonterminal(currentNt, separator.asList(), false);

            //Create a recursive production from the body and separator nonterminals.
            bRecursiveProduction = converter.createProduction(ParserNextArrayAction.WITH_SEPARATOR,
                    bNonterminal, bSeparatorNonterminal, bBodyNonterminal);
        } else {
            //Create a recursive production from the body nonterminal.
            bRecursiveProduction = converter.createProduction(ParserNextArrayAction.WITHOUT_SEPARATOR,
                    bNonterminal, bBodyNonterminal);
        }

        //Initialize result nonterminal productions.
        List<BnfProduction> bProductions = new ArrayList<>();
        bProductions.add(bTerminalProduction);
        bProductions.add(bRecursiveProduction);
        bNonterminal.setProductions(bProductions);

        return bNonterminal;
    }

    @Override
    public <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processRepetitionElement(this);
    }

    @Override
    public String toString() {
        return "(" + getBody() + ")" + (nullable ? "*" : "+");
    }
}
