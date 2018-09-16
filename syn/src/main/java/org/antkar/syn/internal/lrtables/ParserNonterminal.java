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
 * Parser nonterminal descriptor.
 */
public final class ParserNonterminal {
    private final String name;
    private final boolean extended;

    ParserNonterminal(String name, boolean extended) {
        this.name = Checks.notNull(name);
        this.extended = extended;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns <code>true</code> if this is an extended start nonterminal.
     */
    boolean isExtended() {
        return extended;
    }

    @Override
    public String toString() {
        return name;
    }
}
