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

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.Checks;
import org.antkar.syn.internal.CommonUtil;
import org.antkar.syn.internal.parser.ParserEngine;

/**
 * Parser configuration. Defines LR tables and other information used by {@link ParserEngine} to
 * parse a text.
 */
public final class ParserConfiguration {
    private final Map<String, ParserState> startStates;
    private final List<ParserState> states;
    private final List<TokenDescriptor> tokenDescriptors;

    ParserConfiguration(
            Map<String, ParserState> startStates,
            List<ParserState> states,
            List<TokenDescriptor> tokenDescriptors)
    {
        Checks.notNull(startStates);
        Checks.notNull(states);
        Checks.notNull(tokenDescriptors);

        this.startStates = CommonUtil.unmodifiableMapCopy(startStates);
        this.states = CommonUtil.unmodifiableListCopy(states);
        this.tokenDescriptors = CommonUtil.unmodifiableListCopy(tokenDescriptors);
    }

    /**
     * Returns the start LR state for a given start nonterminal.
     */
    public ParserState getStartState(String name) {
        ParserState result = startStates.get(name);
        return result;
    }

    /**
     * Returns the list of token descriptors used in the grammar.
     */
    public List<TokenDescriptor> getTokenDescriptors() {
        return tokenDescriptors;
    }

    /**
     * Prints LR states to a stream. For debug purposes.
     */
    void print(PrintStream out) {
        for (ParserState state : states) {
            state.print(out);
        }
    }
}
