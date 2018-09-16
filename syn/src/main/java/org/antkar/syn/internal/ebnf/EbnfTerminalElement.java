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
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.Checks;
import org.antkar.syn.internal.bnf.BnfElement;
import org.antkar.syn.internal.bnf.BnfTerminal;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;
import org.antkar.syn.internal.parser.IParserGetter;
import org.antkar.syn.internal.parser.ParserStackGetter;

/**
 * Terminal EBNF element.
 */
public final class EbnfTerminalElement extends EbnfElement {
    private final TokenDescriptor tokenDescriptor;

    public EbnfTerminalElement(String key, TextPos keyPos, TokenDescriptor descriptor) {
        super(key, keyPos);
        tokenDescriptor = Checks.notNull(descriptor);
    }

    /**
     * Returns the token descriptor associated with this terminal element.
     */
    public TokenDescriptor getTokenDescriptor() {
        return tokenDescriptor;
    }

    @Override
    public String toString() {
        String string = tokenDescriptor.toString();
        return string;
    }

    @Override
    public BnfElement convert(EbnfToBnfConverter converter, String currentNt) {
        BnfTerminal bTerminal = converter.convertTerminal(tokenDescriptor);
        return bTerminal;
    }

    @Override
    public boolean isValuableElement() {
        boolean valuable = tokenDescriptor.getType().isLiteral();
        return valuable;
    }

    @Override
    public IParserGetter getGetter(int offset) {
        return new ParserStackGetter(offset);
    }

    @Override
    public <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processTerminalElement(this);
    }
}
