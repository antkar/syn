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

import org.antkar.syn.BnfElement;
import org.antkar.syn.EbnfToBnfConverter;
import org.antkar.syn.IParserGetter;
import org.antkar.syn.ParserConstGetter;
import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.ValueNode;

/**
 * Value EBNF element.
 */
class EbnfValueElement extends EbnfElement {
    private final ValueNode userNode;
    
    EbnfValueElement(String key, TextPos keyPos, ValueNode userNode) {
        super(key, keyPos);
        this.userNode = userNode;
    }
    
    @Override
    BnfElement convert(EbnfToBnfConverter converter, String currentNt) {
        //A value element does not produce a BNF element.
        return null;
    }
    
    @Override
    IParserGetter getGetter(int offset) {
        return new ParserConstGetter(userNode);
    }

    @Override
    <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processValueElement(this);
    }
    
    /**
     * Returns the associated value node.
     */
    ValueNode getValueNode() {
        return userNode;
    }
    
    @Override
    public String toString() {
        String userStr = String.valueOf(userNode);
        String s = "<" + userStr + ">";
        return s;
    }
}
