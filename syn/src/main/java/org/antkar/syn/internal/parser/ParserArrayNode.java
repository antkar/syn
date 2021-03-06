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
package org.antkar.syn.internal.parser;

import java.util.ArrayList;
import java.util.List;

import org.antkar.syn.ArrayNode;
import org.antkar.syn.SynNode;

/**
 * Array Parser Tree node. Produced by repetition elements.
 */
final class ParserArrayNode implements IParserNode {
    private final List<SynNode> subNodes;

    ParserArrayNode() {
        subNodes = new ArrayList<>();
    }

    /**
     * Adds a sub-node to this array.
     */
    void addSubNode(SynNode subNode) {
        subNodes.add(subNode);
    }

    @Override
    public SynNode createUserNode() {
        SynNode result = new ArrayNode(subNodes);
        return result;
    }

    @Override
    public String toString() {
        return subNodes + "";
    }
}
