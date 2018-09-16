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

import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.Checks;

/**
 * Key-character tree node. {@link KeycharScanner} uses a tree to recognize a key-character in a sequence
 * of characters. Every node has zero or more {@link KeycharTreeLink links} to other nodes. Every link has
 * an associated character. If an input character matches the character of a link, the scanner goes through
 * that link to the next node, recognizing a token thereby.
 */
final class KeycharTreeNode {
    private final TokenDescriptor token;
    private final KeycharTreeLink[] gotos;

    KeycharTreeNode(TokenDescriptor token, KeycharTreeLink[] gotos) {
        this.token = token;
        this.gotos = Checks.notNull(gotos);
    }

    /**
     * Returns the token associated with this node. If not <code>null</code>, reaching this node means that
     * the token has been recognized.
     */
    TokenDescriptor getToken() {
        return token;
    }

    /**
     * Returns the node linked to this one by the specified character. Can return <code>null</code>.
     */
    KeycharTreeNode getLinkedNode(char ch) {
        KeycharTreeNode result = null;
        for (int i = 0; i < gotos.length; ++i) {
            if (gotos[i].getCh() == ch) {
                result = gotos[i].getDestinationNode();
                break;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();

        if (token != null) {
            bld.append(token);
        }
        bld.append("(");
        String sep = "";
        for (KeycharTreeLink scannerGoto : gotos) {
            bld.append(sep);
            bld.append(scannerGoto);
            sep = " | ";
        }
        bld.append(")");

        return bld.toString();
    }
}
