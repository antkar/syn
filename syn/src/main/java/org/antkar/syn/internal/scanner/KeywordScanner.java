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

/**
 * Keyword scanner. Extends an identifier scanner and translates identifiers into keywords.
 */
class KeywordScanner extends IdScanner {
    private final ScannerConfiguration config;
    private final TokenNodePrimitiveResult primitiveResult;
    
    KeywordScanner(ScannerConfiguration config) {
        this.config = config;
        primitiveResult = new TokenNodePrimitiveResult(null);
    }

    @Override
    IPrimitiveResult getResult(String literal) {
        //Lookup a keyword.
        TokenDescriptor tokenDescriptor = config.getKeyword(literal);
        if (tokenDescriptor != null) {
            //Keyword found. Return that keyword.
            primitiveResult.setTokenDescriptor(tokenDescriptor);
            return primitiveResult;
        }
        
        //The identifier is not a keyword. Return the identifier token.
        return super.getResult(literal);
    }
}
