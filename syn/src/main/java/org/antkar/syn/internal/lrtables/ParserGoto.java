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

import org.antkar.syn.internal.Checks;

/**
 * GOTO transition - a transition from one LR state to another by a nonterminal.
 */
final class ParserGoto {
    final ParserNonterminal nonterminal;
    final ParserState state;

    ParserGoto(ParserNonterminal nonterminal, ParserState state) {
        this.nonterminal = Checks.notNull(nonterminal);
        this.state = Checks.notNull(state);
    }

    @Override
    public String toString() {
        return nonterminal + " -> " + state.getIndex();
    }
}
