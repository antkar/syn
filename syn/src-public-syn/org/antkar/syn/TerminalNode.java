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

import java.io.PrintStream;

import org.antkar.syn.PosBuffer;
import org.antkar.syn.TokenNode;

/**
 * Terminal node - a node that does not contain child nodes. Produced by terminal grammar symbols and constants.
 * Has two direct subclasses: {@link TokenNode} and {@link ValueNode}.
 */
public abstract class TerminalNode extends SynNode {
    
    //Non-token nodes (value nodes) do not have position, but the number of such nodes is limited - a fixed set
    //for a given grammar. For this reason keeping empty text position fields for such nodes must not cause
    //a visible overhead. The number of token nodes, by contrast, is unlimited and depends on an input,
    //not a grammar.
    private final SourceDescriptor posSource;
    private final int posOffset;
    private final int posLine;
    private final int posColumn;
    private final int posLength;
    
    /**
     * Constructor.
     * 
     * @param pos the reference to a text position container, or <code>null</code> if the node is not associated
     * with an input position.
     */
    TerminalNode(PosBuffer pos) {
        if (pos != null) {
            posSource = pos.getSource();
            posOffset = pos.getOffset();
            posLine = pos.getLine();
            posColumn = pos.getColumn();
            posLength = pos.getLength();
        } else {
            posSource = null;
            posOffset = -1;
            posLine = -1;
            posColumn = -1;
            posLength = 0;
        }
    }
    
    /**
     * Returns the corresponding token descriptor.
     * <ul>
     * <li>If this node was produced by a terminal grammar symbol, the token descriptor of that symbol
     * is returned.</li>
     * <li>If the node was produced by a constant of integer, floating-point or string type, the descriptor
     * of the corresponding literal token is returned (e. g. {@link TokenDescriptor#INTEGER}).</li>
     * <li>Otherwise, <code>null</code> is returned.
     * </ul>
     * 
     * @return the token descriptor.
     */
    public abstract TokenDescriptor getTokenDescriptor();
    
    /**
     * If this token node was produced by a custom keyword or a key-character symbol, the literal
     * of that symbol is returned. Otherwise, the return value is the same as for
     * {@link ValueNode#getString()}.
     * 
     * @return the string value.
     * @throws UnsupportedOperationException if this node is a value and its type is not a string.
     */
    public abstract String getString();
    
    /**
     * Returns the input position associated with this node.
     * 
     * @return the input position, or <code>null</code> if the node does not have an associated input position.
     */
    public final TextPos getPos() {
        return posSource == null ? null : new TextPos(posSource, posOffset, posLine, posColumn, posLength);
    }
    
    @Override
    void print(PrintStream out, int level) {
        out.println(this);
    }
    
    @Override
    public abstract String toString();
}
