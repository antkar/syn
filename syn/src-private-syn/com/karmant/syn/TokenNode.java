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
 * Token node. Is a terminal node which is created by the parser to represent one of custom terminal symbols
 * or the end-of-file special symbol. For literal terminal symbols (like integer literal or identifier), a
 * {@link ValueNode} is created instead. 
 */
class TokenNode extends TerminalNode {
    private final TokenDescriptor tokenDescriptor;

    TokenNode(PosBuffer pos, TokenDescriptor tokenDescriptor) {
        super(pos);
        assert tokenDescriptor != null;
        this.tokenDescriptor = tokenDescriptor;
    }

    @Override
    public TokenDescriptor getTokenDescriptor() {
        return tokenDescriptor;
    }
    
    @Override
    public String getString() {
        return tokenDescriptor.getLiteral();
    }

    @Override
    public String toString() {
        String toString = tokenDescriptor.toString();
        return toString;
    }
}
