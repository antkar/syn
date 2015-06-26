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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antkar.syn.SynParser;

/**
 * Ambiguity Comparator. Determines which of two conflicting Abstract Syntax Trees has to be kept and which
 * has to be rejected. Used for automatic ambiguities resolution when {@link SynParser}'s fail-on-ambiguity
 * option if turned off.
 */
final class AmbiguityComparator {
    private AmbiguityComparator(){}
    
    /**
     * Compare two conflicting stacks. The stacks must be of the same length. (Otherwise they cannot conflict,
     * because parser state chains must be equal).
     * 
     * @param o1 the first stack.
     * @param o2 the second stack.
     * 
     * @return a positive value, a negative value or <code>0</code> if, respectively, the first stack has to be
     * kept, the second stack has to be kept or if the "winner" is undefined.
     */
    static int compare(ParserStack o1, ParserStack o2) {
        //Create ambiguity tree for each stack.
        
        List<AmbiguityNode> list1 = new ArrayList<>();
        List<AmbiguityNode> list2 = new ArrayList<>();
        
        //Add differing stack elements to lists.
        ParserStackElement top1 = o1.getTop();
        ParserStackElement top2 = o2.getTop();
        while (top1 != top2) {
            //Go through the stacks until the branching point is reached.
            list1.add(top1.createAmbiguityNode());
            list2.add(top2.createAmbiguityNode());
            top1 = top1.getPrev();
            top2 = top2.getPrev();
        }
        
        //Create trees from nodes.
        AmbiguityNode tree1 = reversedListToNode(list1);
        AmbiguityNode tree2 = reversedListToNode(list2);
        
        //Compare ambiguity trees.
        int result = AmbiguityNode.compare(tree1, tree2);
        return result;
    }
    
    /**
     * Creates an ambiguity node from the specified sub-nodes.
     */
    private static AmbiguityNode reversedListToNode(List<AmbiguityNode> list) {
        Collections.reverse(list);
        AmbiguityNode[] nodes = list.toArray(new AmbiguityNode[list.size()]);
        AmbiguityNode result = new AmbiguityNode(nodes);
        return result;
    }
}
