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

import org.antkar.syn.SynException;

/**
 * <p>A primitive scanner for a certain lexical construction, like an identifier or an integer literal.
 * A combination of primitive scanners gives a complete lexical analyzer.</p>
 * 
 * <p>Each primitive scanner decides whether to process the input by examining one or two characters
 * of the input, but not more. In other words, a primitive scanner makes the decision without removing a
 * character from a {@link LookaheadCharStream}, so it is not necessary to roll back the input.</p>
 */
interface IPrimitiveScanner {
    /**
     * Scans the next token from the input.
     * 
     * @param context the lexical analyzer context.
     * @return the token information, or <code>null</code> if the input does not match the token
     * supported by this primitive scanner.
     * @throws SynException if the input is incorrect or an I/O error occurs.
     */
    IPrimitiveResult scan(PrimitiveContext context) throws SynException;
}
