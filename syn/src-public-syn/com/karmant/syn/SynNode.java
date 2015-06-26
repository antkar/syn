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
package com.karmant.syn;

import java.io.PrintStream;

/**
 * Abstract Syntax Tree (AST) node. Has three subtypes:
 * <li>{@link TerminalNode},</li>
 * <li>{@link ArrayNode},</li>
 * <li>{@link ObjectNode}.</li>
 */
public abstract class SynNode {
    SynNode(){}
    
    /**
     * Prints the syntax tree (considering this node the root) to the specified print stream. Used for
     * debugging purposes.
     * 
     * @param out the stream to print the tree to.
     */
    public void print(PrintStream out) {
        if (out == null) {
            throw new NullPointerException("out");
        }
        print(out, 0);
    }
    
    /**
     * Prints the tree to the specified stream with the specified indentation level.
     * 
     * @param out the stream.
     * @param level the indentation level; must be equal or greater than 0.
     */
    abstract void print(PrintStream out, int level);
}
