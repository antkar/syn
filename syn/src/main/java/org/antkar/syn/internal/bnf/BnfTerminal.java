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

import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.Checks;

/**
 * Terminal BNF element.
 */
public final class BnfTerminal extends BnfElement {
    private final TokenDescriptor tokenDescriptor;

    public BnfTerminal(int elementIndex, TokenDescriptor descriptor) {
        super(elementIndex);
        tokenDescriptor = Checks.notNull(descriptor);
    }

    /**
     * Returns the token descriptor associated with this terminal.
     */
    public TokenDescriptor getTokenDescriptor() {
        return tokenDescriptor;
    }

    @Override
    public String toString() {
        return tokenDescriptor.toString();
    }
}
