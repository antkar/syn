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
 * Temporary LR state. Used during LR states generation process.
 */
final class ParserTempState {
    private final ParserItemSet itemSet;
    private final ParserState state;

    ParserTempState(ParserItemSet itemSet, ParserState state) {
        this.itemSet = Checks.notNull(itemSet);
        this.state = Checks.notNull(state);
    }

    ParserItemSet getItemSet() {
        return itemSet;
    }

    ParserState getState() {
        return state;
    }

    @Override
    public String toString() {
        return state + " " + itemSet;
    }
}
