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
package org.antkar.syn.internal.scanner;

import java.util.Collections;
import java.util.Map;

import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.Checks;

/**
 * Scanner configuration. Defines what tokens have to be recognized by a lexical analyzer.
 */
public final class ScannerConfiguration {
    private final boolean floatingPoint;
    private final Map<String, TokenDescriptor> keywordMap;
    private final KeycharTreeNode keycharTreeRoot;

    ScannerConfiguration(
            boolean floatingPoint,
            Map<String, TokenDescriptor> keywordMap,
            KeycharTreeNode keycharTreeRoot)
    {
        Checks.notNull(keywordMap);
        Checks.notNull(keycharTreeRoot);

        //Initialize fields.
        this.floatingPoint = floatingPoint;
        this.keywordMap = Collections.unmodifiableMap(keywordMap);
        this.keycharTreeRoot = keycharTreeRoot;
    }

    /**
     * Finds a keyword by its literal.
     * @return the keyword token descriptor, or <code>null</code>, if there is no such keyword.
     */
    TokenDescriptor getKeyword(String literal) {
        Checks.notNull(literal);
        TokenDescriptor tokenDescriptor = keywordMap.get(literal);
        return tokenDescriptor;
    }

    /**
     * Returns the root node of the key-character tree.
     */
    KeycharTreeNode getKeycharTreeRoot() {
        return keycharTreeRoot;
    }

    /**
     * @return <code>true</code> if floating point literal token is used in the grammar, indicating that
     * lexical analyzer must support floating-point numbers.
     */
    boolean isFloatingPoint() {
        return floatingPoint;
    }
}
