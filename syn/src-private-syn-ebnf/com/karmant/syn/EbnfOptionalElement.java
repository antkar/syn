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

import java.util.List;

/**
 * EBNF optional element.
 */
class EbnfOptionalElement extends EbnfEmbeddedElement {
    EbnfOptionalElement(String key, TextPos keyPos, EbnfProductions body) {
        super(key, keyPos, body);
    }

    @Override
    BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException {
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
    <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processOptionalElement(this);
    }
    
    @Override
    public String toString() {
        return "(" + getBody() + ")?";
    }
}
