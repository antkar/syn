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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is responsible for creating a {@link ScannerConfiguration scanner configuration} for a
 * particular grammar.
 */
final class ScannerConfigurator {
    private ScannerConfigurator(){}
    
    /**
     * Creates a scanner configuration.
     * 
     * @param tokens the collection of tokens used in the grammar.
     * @return the configuration.
     */
    static ScannerConfiguration makeConfiguration(Collection<TokenDescriptor> tokens) {
        assert tokens != null;
        
        //Divide tokens into literal tokens, keywords and key-characters.
        Set<TokenType> literalTokens = new HashSet<>(); 
        Map<String, TokenDescriptor> keywordMap = new HashMap<>();
        Map<String, TokenDescriptor> keycharMap = new HashMap<>();
        categorizeTokens(tokens, literalTokens, keywordMap, keycharMap);
        
        boolean floatingPoint = literalTokens.contains(TokenType.FLOAT);
        
        //Build key-character tree.
        KeycharTreeNode keycharTreeRoot = makeKeycharTree(keycharMap);
        
        //Create a result.
        ScannerConfiguration result = new ScannerConfiguration(floatingPoint, keywordMap, keycharTreeRoot);
        return result;
    }

    /**
     * Divides tokens into categories.
     */
    private static void categorizeTokens(
            Collection<TokenDescriptor> tokens,
            Set<TokenType> literalTokens,
            Map<String, TokenDescriptor> keywordMap,
            Map<String, TokenDescriptor> keycharMap)
    {
        for (TokenDescriptor token : tokens) {
            TokenType tokenType = token.getType();
            if (tokenType.isLiteral()) {
                literalTokens.add(tokenType);
            } else if (TokenType.KEYCHAR.equals(tokenType)) {
                keycharMap.put(token.getLiteral(), token);
            } else if (TokenType.KEYWORD.equals(tokenType)) {
                keywordMap.put(token.getLiteral(), token);
            }
        }
    }
    
    /**
     * Creates a key-character tree.
     * @param keycharMap the map between a token's literal and its descriptor.
     * @return the root node of the tree.
     */
    private static KeycharTreeNode makeKeycharTree(Map<String, TokenDescriptor> keycharMap) {
        Set<String> keySet = keycharMap.keySet();
        String[] literals = keySet.toArray(new String[keySet.size()]);
        
        //Sort the literals in alphabetical order, so that literals starting with the same prefix are
        //subsequent.
        Arrays.sort(literals);
        
        //Build the tree recursively.
        KeycharTreeNode result = makeKeycharTreeNode(literals, 0, 0, literals.length, keycharMap);
        return result;
    }
    
    /**
     * Creates a key-character subtree for a given set of literals. A set is defined as a sub-array of
     * the literals array.
     * 
     * @param literals an array of alphabetically sorted literals.
     * @param ofs the offset of the character being examined.
     * @param start the start position of the literals set to process.
     * @param end the end position of the literals set to process. The literal at that position is not
     * included into the set.
     * @param keycharMap map of key-character tokens.
     * 
     * @return the root node of the subtree.
     */
    private static KeycharTreeNode makeKeycharTreeNode(
            String[] literals,
            int ofs,
            int start,
            int end, 
            Map<String, TokenDescriptor> keycharMap) 
    {
        List<KeycharTreeLink> linkList = new ArrayList<>();
        
        TokenDescriptor tokenDescriptor = null;
        
        //Go through all literals in the set.
        int pos = start;
        while (pos < end) {
            String literal = literals[pos];
            if (literal.length() == ofs) {
                //End of a literal is reached - a token has to be associated with the tree node.
                tokenDescriptor = keycharMap.get(literal);
                //Skip this literal.
                ++pos;
            } else {
                char ch = literal.charAt(ofs);
                
                //Find all literals starting with the same prefix.
                int next = getCharBlockEnd(literals, ofs + 1, pos);
                
                //Recursively create a subtree for those literals.
                KeycharTreeNode state = makeKeycharTreeNode(literals, ofs + 1, pos, next, keycharMap);
                KeycharTreeLink link = new KeycharTreeLink(ch, state);
                linkList.add(link);
                
                //Skip literals.
                pos = next;
            }
        }
        
        //Create the subtree root node.
        KeycharTreeLink[] links = linkList.toArray(new KeycharTreeLink[linkList.size()]); 
        KeycharTreeNode result = new KeycharTreeNode(tokenDescriptor, links);
        return result;
    }

    /**
     * Finds the end of the sequence of literals starting with the same prefix of the specified length.
     * 
     * @param literals the array of literals.
     * @param len the length of the prefix.
     * @param start the position in the array of literals where to start looking for the end.
     * @return the end position (literal at that position is not included to the sequence).
     */
    private static int getCharBlockEnd(String[] literals, int len, int start) {
        String left = literals[start].substring(0, len);
        int result = start + 1;
        while (result < literals.length) {
            if (!literals[result].startsWith(left)) {
                break;
            }
            ++result;
        }
        return result;
    }
}
