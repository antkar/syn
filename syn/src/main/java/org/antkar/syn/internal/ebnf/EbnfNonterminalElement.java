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

import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.internal.bnf.BnfElement;
import org.antkar.syn.internal.bnf.BnfNonterminal;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;

/**
 * Nonterminal EBNF element.
 */
public final class EbnfNonterminalElement extends EbnfElement {
    private final EbnfNonterminal nonterminal;

    public EbnfNonterminalElement(String key, TextPos keyPos, EbnfNonterminal nonterminal) {
        super(key, keyPos);
        assert nonterminal != null;
        this.nonterminal = nonterminal;
    }

    /**
     * Returns the nonterminal referenced by this element.
     */
    public EbnfNonterminal getNonterminal() {
        return nonterminal;
    }

    @Override
    public BnfElement convert(EbnfToBnfConverter converter, String currentNt) throws SynException {
        BnfNonterminal bNonterminal = converter.convertNonterminal(nonterminal);
        return bNonterminal;
    }

    @Override
    public String toString() {
        return nonterminal.toString();
    }

    @Override
    public <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processNonterminalElement(this);
    }
}
