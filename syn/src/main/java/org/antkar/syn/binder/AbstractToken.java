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
package org.antkar.syn.binder;

import org.antkar.syn.TextPos;

/**
 * Used by {@link SynBinder} to represent a pair of a text position and a value of a token of a particular type.
 */
public abstract class AbstractToken {
    private final TextPos pos;
    
    AbstractToken(TextPos pos) {
        this.pos = pos;
    }
    
    /**
     * Returns the text position associated with this value. If this value was produced by a constant
     * grammar element, the position is <code>null</code>. Terminal symbol grammar elements produce values with
     * non-<code>null</code> positions.
     * @return the text position.
     */
    public TextPos getPos() {
        return pos;
    }
}
