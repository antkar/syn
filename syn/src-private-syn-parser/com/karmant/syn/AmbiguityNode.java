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
 * Ambiguity Tree node. An Ambiguity Tree is created whenever a grammar ambiguity has to be resolved.
 */
class AmbiguityNode {
    static final AmbiguityNode[] EMPTY_ARRAY = new AmbiguityNode[0];
    static final AmbiguityNode TERMINAL = new AmbiguityNode(1, EMPTY_ARRAY);
    static final AmbiguityNode NULL = new AmbiguityNode(0, EMPTY_ARRAY);
    
    /** The number of tokens covered by this node and all its sub-nodes. */
    private final int tokenCount;
    
    /** Sub-nodes. */
    private final AmbiguityNode[] subNodes;

    AmbiguityNode(AmbiguityNode[] subNodes) {
        assert subNodes != null;
        this.subNodes = subNodes;
        this.tokenCount = calcTokenCount(subNodes);
    }
    
    private AmbiguityNode(int size, AmbiguityNode[] subNodes) {
        this.tokenCount = size;
        this.subNodes = subNodes;
    }
    
    @Override
    public String toString() {
        String result = "(" + tokenCount;
        for (AmbiguityNode subNode : subNodes) {
            result += " " + subNode;
        }
        result += ")";
        return result;
    }
    
    private static int calcTokenCount(AmbiguityNode[] subNodes) {
        int result = 0;
        for (AmbiguityNode subNode : subNodes) {
            result += subNode.tokenCount;
        }
        return result;
    }
    
    /**
     * <p>Determines which of two Ambiguity Trees has to be kept and which to be rejected.</p>
     * 
     * <p>The idea is to prefer a production which has more tokens covered by its leftmost elements.
     * This is similar to the approach used by YACC for resolving conflicts: a SHIFT-REDUCE conflict is
     * resolved to SHIFT, i. e. more tokens are kept in the current nonterminal and less in the following
     * one.</p>
     * 
     * <p>The main feature of this algorithm is that it resolves the Dangling Else ambiguity correctly,
     * binding the "else" clause to the innermost "if".</p>
     * 
     * @param o1 the first tree.
     * @param o2 the second tree.
     * 
     * @return a positive value, a negative value or <code>0</code> if, respectively, the first tree has to be
     * kept, the second tree has to be kept or if the "winner" is undefined.
     */
    static int compare(AmbiguityNode o1, AmbiguityNode o2) {
        int result = Integer.compare(o1.tokenCount, o2.tokenCount);
        if (result != 0) {
            //Numbers of tokens covered by the nodes are different. Prefer the node with more tokens.
            return result;
        }

        //Compare sub-nodes.
        int n = Math.min(o1.subNodes.length, o2.subNodes.length);
        for (int i = 0; i < n; ++i) {
            result = compare(o1.subNodes[i], o2.subNodes[i]);
            if (result != 0) {
                return result;
            }
        }
        
        //No difference between sub-nodes. Prefer the node with less sub-nodes.
        result = Integer.compare(o2.subNodes.length, o1.subNodes.length);
        return result;
    }
}
