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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Thrown to indicate a syntax error - a situation when the input does not match the grammar.
 */
public class SynSyntaxException extends SynTextException {
    private static final long serialVersionUID = -135689230394245417L;
    
    private final TerminalNode actualToken;
    private final List<TokenDescriptor> expectedTokens;
    
    /**
     * Constructs an exception.
     * 
     * @param textPos the text position in the input where the mismatching was detected.
     * @param actualToken the actual input token.
     * @param expectedTokens the collection of allowed input tokens for the position.
     */
    SynSyntaxException(TextPos textPos, TerminalNode actualToken, Collection<TokenDescriptor> expectedTokens) {
        super(textPos, constructMessage(actualToken, expectedTokens));
        assert actualToken != null;
        assert expectedTokens != null;
        
        this.actualToken = actualToken;
        List<TokenDescriptor> expectedTokenList = new ArrayList<>(expectedTokens);
        Collections.sort(expectedTokenList, new Comparator<TokenDescriptor>() {
            @Override
            public int compare(TokenDescriptor o1, TokenDescriptor o2) {
                int result = o1.getType().compareTo(o2.getType());
                if (result == 0) {
                    if (o1.getLiteral() != null && o2.getLiteral() != null) {
                        result = o1.getLiteral().compareTo(o2.getLiteral());
                    } else if (o1.getLiteral() != null) {
                        result = 1;
                    } else if (o2.getLiteral() != null) {
                        result = -1;
                    }
                }
                return result;
            }
        });
        this.expectedTokens = Collections.unmodifiableList(expectedTokenList);
    }
    
    /**
     * Returns the actual input token that did not match the grammar.
     * @return the token which caused the exception.
     */
    public TerminalNode getActualToken() {
        return actualToken;
    }
    
    /**
     * Returns the list of expected tokens.
     * @return the list of expected tokens.
     */
    public List<TokenDescriptor> getExpectedTokens() {
        return expectedTokens;
    }
    
    private static String constructMessage(
            TerminalNode actualToken,
            Collection<TokenDescriptor> expectedTokens)
    {
        assert actualToken != null;
        assert expectedTokens != null;
        return "Syntax error on token " + actualToken;
    }
}
