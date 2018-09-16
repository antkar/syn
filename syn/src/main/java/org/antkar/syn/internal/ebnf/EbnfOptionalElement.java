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

import java.util.List;

import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.internal.bnf.BnfElement;
import org.antkar.syn.internal.bnf.BnfNonterminal;
import org.antkar.syn.internal.bnf.BnfProduction;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;
import org.antkar.syn.internal.parser.IParserAction;
import org.antkar.syn.internal.parser.ParserNullAction;
import org.antkar.syn.internal.parser.ParserObjectAction;

/**
 * EBNF optional element.
 */
public final class EbnfOptionalElement extends EbnfEmbeddedElement {
    public EbnfOptionalElement(String key, TextPos keyPos, EbnfProductions body) {
        super(key, keyPos, body);
    }

    @Override
    public BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException {
        //Convert productions.
        boolean hasEmbeddedObject = hasEmbeddedObject();
        List<BnfProduction> bProductions =
                converter.convertProductions(currentNt, getBody().asList(), hasEmbeddedObject);

        //Add empty production.
        IParserAction nullAction = hasEmbeddedObject ? ParserObjectAction.NULL : ParserNullAction.INSTANCE;
        BnfProduction bEmptyProduction = converter.createProduction(nullAction);
        bProductions.add(bEmptyProduction);

        //Create an new anonymous nonterminal.
        BnfNonterminal bNonterminal = converter.createAnonymousNonterminal(currentNt, bProductions);
        return bNonterminal;
    }

    @Override
    public <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processOptionalElement(this);
    }

    @Override
    public String toString() {
        return "(" + getBody() + ")?";
    }
}
