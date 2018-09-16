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
package org.antkar.syn.internal.bnf;

import org.antkar.syn.internal.Checks;

/**
 * An element of a BNF grammar. Can be either a terminal or a nonterminal element.
 */
public abstract class BnfElement {
    private final int elementIndex;

    BnfElement(int elementIndex) {
        Checks.argument(elementIndex >= 0);
        this.elementIndex = elementIndex;
    }

    /**
     * Returns the index of this element in the grammar.
     */
    public final int getElementIndex() {
        return elementIndex;
    }
}
