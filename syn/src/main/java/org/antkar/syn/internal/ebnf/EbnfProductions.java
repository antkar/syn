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

import org.antkar.syn.internal.Checks;
import org.antkar.syn.internal.CommonUtil;

/**
 * A list of EBNF productions.
 */
public final class EbnfProductions {
    private final List<EbnfProduction> productions;

    public EbnfProductions(List<EbnfProduction> productions) {
        Checks.notNull(productions);
        this.productions = CommonUtil.unmodifiableListCopy(productions);
    }

    /**
     * Returns the list of productions.
     */
    public List<EbnfProduction> asList() {
        return productions;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();

        String sep = "";
        for (EbnfProduction production : productions) {
            bld.append(sep);
            bld.append(production);
            sep = " | ";
        }

        String result = bld.toString();
        return result;
    }
}
