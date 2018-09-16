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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * EBNF production.
 */
public final class EbnfProduction {
    private EbnfNonterminal nonterminal = null;
    private final List<EbnfElement> elements;

    public EbnfProduction(List<EbnfElement> elements) {
        assert elements != null;
        this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
    }

    /**
     * Sets the nonterminal which this production belongs to. Cannot be initialized in the constructor, because
     * there may be recursive dependencies between nonterminals.
     */
    void setNonterminal(EbnfNonterminal nonterminal) {
        assert nonterminal != null;
        assert this.nonterminal == null;
        this.nonterminal = nonterminal;
    }

    /**
     * Returns the nonterminal which this production belongs to.
     */
    public EbnfNonterminal getNonterminal() {
        assert nonterminal != null;
        return nonterminal;
    }

    /**
     * Returns the elements of this production.
     */
    public List<EbnfElement> getElements() {
        return elements;
    }

    /**
     * Returns <code>true</code> if this production contains an embedded object.
     * @see {@link EbnfEmbeddedElement}.
     */
    boolean hasEmbeddedObject() {
        for (EbnfElement element : elements) {
            if (element.getAttribute() != null || element.hasEmbeddedObject()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();

        //Append nonterminal.
        if (nonterminal != null) {
            bld.append(nonterminal.getName());
            bld.append(": ");
        }

        //Append elements.
        String sep = "";
        for (EbnfElement element : elements) {
            bld.append(sep);
            bld.append(element);
            sep = " ";
        }

        //Return result.
        String string = bld.toString();
        return string;
    }
}
