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
package org.antkar.syn.sample.script.schema;

import org.antkar.syn.SynField;
import org.antkar.syn.TextPos;

/**
 * Script <code>for</code> statement syntax node.
 */
public abstract class ForStatement extends Statement {
    /** The text position of the first token. */
    @SynField
    private TextPos synPos;

    public ForStatement(){}

    @Override
    TextPos getStartTextPos() {
        return synPos;
    }
    
    @Override
    public String toString() {
        return "for (...)";
    }
}