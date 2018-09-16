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
package org.antkar.syn.internal.ebnf;

import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.ValueNode;
import org.antkar.syn.internal.bnf.BnfElement;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;
import org.antkar.syn.internal.parser.IParserGetter;
import org.antkar.syn.internal.parser.ParserConstGetter;

/**
 * Value EBNF element.
 */
public final class EbnfValueElement extends EbnfElement {
    private final ValueNode userNode;

    public EbnfValueElement(String key, TextPos keyPos, ValueNode userNode) {
        super(key, keyPos);
        this.userNode = userNode;
    }

    @Override
    public BnfElement convert(EbnfToBnfConverter converter, String currentNt) {
        //A value element does not produce a BNF element.
        return null;
    }

    @Override
    public IParserGetter getGetter(int offset) {
        return new ParserConstGetter(userNode);
    }

    @Override
    public <T> T invokeProcessor(EbnfElementProcessor<T> processor) throws SynException {
        return processor.processValueElement(this);
    }

    /**
     * Returns the associated value node.
     */
    public ValueNode getValueNode() {
        return userNode;
    }

    @Override
    public String toString() {
        String userStr = String.valueOf(userNode);
        String s = "<" + userStr + ">";
        return s;
    }
}
