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

/**
 * Token stream interface. Represents a lexical analyzer used by a parser.
 */
public interface TokenStream {
    
    /**
     * Scans the next input token. If succeeds, the scanned token becomes the current one.
     * 
     * @throws SynLexicalException if a token cannot be recognized in the input.
     * @throws SynException if another error occurs (like I/O error).
     */
    void nextToken() throws SynException;
    
    /**
     * Returns the token descriptor of the current token (which was scanned by the last {@link #nextToken()}
     * invocation.
     * 
     * @return the token descriptor.
     */
    TokenDescriptor getTokenDescriptor();
    
    /**
     * Returns the offset of the first character of the current token, relative to the beginning of the input.
     * @return the start offset.
     */
    int getTokenStartOffset();
    
    /**
     * Returns the offset of the first character after the current token, relative to the beginning of the input.
     * @return the end offset.
     */
    int getTokenEndOffset();
    
    /**
     * Returns the offset of the first unprocessed character, relative to the beginning of the input.
     * @return the current offset.
     */
    int getCurrentOffset();
}
