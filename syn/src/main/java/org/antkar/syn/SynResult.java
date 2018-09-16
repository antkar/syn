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
package org.antkar.syn;

import org.antkar.syn.internal.Checks;

/**
 * The result returned by a {@link SynParser}. Contains an Abstract Syntax Tree and some statistics information,
 * such as total line and character count.
 */
public final class SynResult {
    private final SourceDescriptor sourceDescriptor;
    private final int lineCount;
    private final int charCount;
    private final SynNode rootNode;

    /**
     * Initializing constructor.
     */
    public SynResult(SourceDescriptor sourceDescriptor, SynNode rootNode, int lineCount, int charCount) {
        Checks.argument(lineCount >= 0);
        Checks.argument(charCount >= 0);

        this.sourceDescriptor = Checks.notNull(sourceDescriptor);
        this.rootNode = rootNode;
        this.lineCount = lineCount;
        this.charCount = charCount;
    }

    /**
     * Returns the number of characters in the parsed input.
     * @return the number of characters.
     */
    public int getCharCount() {
        return charCount;
    }

    /**
     * Returns the number of lines in the parsed input.
     * @return the number of lines.
     */
    public int getLineCount() {
        return lineCount;
    }

    /**
     * Returns the root node of the Abstract Syntax Tree - the node returned by the start nonterminal symbol.
     * @return the root node. Can be <code>null</code> if the start nonterminal returned <code>null</code>.
     */
    public SynNode getRootNode() {
        return rootNode;
    }

    /**
     * Returns the source descriptor of the parsed input.
     * @return the source descriptor of the input.
     */
    public SourceDescriptor getSourceDescriptor() {
        return sourceDescriptor;
    }

    @Override
    public String toString() {
        return rootNode + "";
    }
}
