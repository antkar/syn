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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antkar.syn.TokenType;

/**
 * Maps an identifier (used in a grammar) to the corresponding literal token's type.
 */
final class TokenTypeResolver {
    private static final Map<String, TokenType> TOKEN_TYPE_MAP;
    
    static {
        Map<String, TokenType> map = new HashMap<>();
        map.put("ID", TokenType.ID);
        map.put("INTEGER", TokenType.INTEGER);
        map.put("FLOAT", TokenType.FLOAT);
        map.put("STRING", TokenType.STRING);
        TOKEN_TYPE_MAP = Collections.unmodifiableMap(map);
    }
    
    private TokenTypeResolver() {
    }
    
    static TokenType getTokenType(String name) {
        TokenType tokenType = TOKEN_TYPE_MAP.get(name);
        return tokenType;
    }
}
