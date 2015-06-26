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
 * Primitive result describing a token which does not have an associated value.
 */
class TokenNodePrimitiveResult implements IPrimitiveResult {
    private TokenDescriptor tokenDescriptor;
    
    TokenNodePrimitiveResult(TokenDescriptor tokenDescriptor) {
        this.tokenDescriptor = tokenDescriptor;
    }
    
    void setTokenDescriptor(TokenDescriptor tokenDescriptor) {
        this.tokenDescriptor = tokenDescriptor;
    }

    @Override
    public TokenDescriptor getTokenDescriptor() {
        return tokenDescriptor;
    }

    @Override
    public TerminalNode createTokenNode(PosBuffer pos) {
        assert tokenDescriptor != null;
        return new TokenNode(pos, tokenDescriptor);
    }
}
