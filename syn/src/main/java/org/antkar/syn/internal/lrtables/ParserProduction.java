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

import org.antkar.syn.internal.parser.IParserAction;

/**
 * Parser production descriptor.
 */
public class ParserProduction {
    private final ParserNonterminal nonterminal;
    private final int length;
    private final IParserAction action;

    ParserProduction(ParserNonterminal nonterminal, int length, IParserAction action) {
        assert nonterminal != null;
        assert length >= 0;
        assert action != null;

        this.nonterminal = nonterminal;
        this.length = length;
        this.action = action;
    }

    public ParserNonterminal getNonterminal() {
        return nonterminal;
    }

    public int getLength() {
        return length;
    }

    public IParserAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return nonterminal + "[" + length + "]{ " + action + " }";
    }
}
