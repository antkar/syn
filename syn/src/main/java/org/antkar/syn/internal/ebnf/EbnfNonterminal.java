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

import org.antkar.syn.internal.Checks;

/**
 * EBNF nonterminal.
 */
public final class EbnfNonterminal {
    private final String name;
    private EbnfProductions productions;

    /**
     * Constructs a nonterminal. Productions are undefined at this point.
     */
    public EbnfNonterminal(String name) {
        Checks.notNull(name);
        Checks.argument(!name.isEmpty());
        this.name = name;

        productions = null;
    }

    /**
     * Sets productions of this nonterminal.
     */
    public void setProductions(EbnfProductions productions) {
        Checks.state(this.productions == null);
        this.productions = Checks.notNull(productions);
        for (EbnfProduction production : productions.asList()) {
            production.setNonterminal(this);
        }
    }

    /**
     * Returns productions of this nonterminal.
     */
    public EbnfProductions getProductions() {
        Checks.notNull(productions);
        return productions;
    }

    /**
     * Returns the name of this nonterminal.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
