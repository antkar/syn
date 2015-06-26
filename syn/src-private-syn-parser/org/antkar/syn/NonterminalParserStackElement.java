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

import java.io.PrintStream;

import org.antkar.syn.CommonUtil;
import org.antkar.syn.ParserProduction;
import org.antkar.syn.ParserState;

/**
 * Nonterminal stack element. Contains sub-elements.
 */
class NonterminalParserStackElement extends ParserStackElement {
    private final ParserProduction production;
    
    /** The top of the stack of sub-elements. */
    private final ParserStackElement subElements;
    
    NonterminalParserStackElement(
            ParserStackElement prev,
            ParserState state,
            ParserProduction production,
            ParserStackElement subElements)
    {
        super(prev, state);
        
        assert production != null;
        assert subElements != null;
        
        this.production = production;
        this.subElements = subElements;
    }

    @Override
    IParserNode createParserNode() {
        IParserAction action = production.getAction();
        IParserNode result = action.execute(subElements);
        return result;
    }
    
    @Override
    AmbiguityNode createAmbiguityNode() {
        int length = production.getLength();
        if (length == 0) {
            return AmbiguityNode.NULL;
        }
        
        AmbiguityNode[] subAmbiguityNodes = new AmbiguityNode[length];
        ParserStackElement element = subElements;
        for (int i = 0; i < subAmbiguityNodes.length; ++i) {
            subAmbiguityNodes[i] = element.createAmbiguityNode();
            element = element.getPrev();
        }
        
        return new AmbiguityNode(subAmbiguityNodes);
    }
    
    @Override
    void print(PrintStream out, int level) {
        CommonUtil.printIndent(out, level);
        out.println(production.getNonterminal().getName());
        
        ParserStackElement[] array = getSubElementsArray();
        for (ParserStackElement element : array) {
            element.print(out, level + 1);
        }
    }
    
    /**
     * Creates an array of sub-elements.
     */
    private ParserStackElement[] getSubElementsArray() {
        int length = production.getLength();
        if (length == 0) {
            return EMPTY_ARRAY;
        }

        ParserStackElement[] result = new ParserStackElement[length];
        ParserStackElement element = subElements;
        for (int i = length - 1; i >= 0; --i) {
            result[i] = element;
            element = element.getPrev();
        }
        
        return result;
    }
}
