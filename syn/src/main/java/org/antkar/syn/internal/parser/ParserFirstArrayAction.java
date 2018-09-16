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

import org.antkar.syn.SynNode;

/**
 * A parser action that creates an array containing a single element. Used in a "terminal" production generated
 * by a repetition element.
 */
public final class ParserFirstArrayAction implements IParserAction {
    public static final ParserFirstArrayAction INSTANCE = new ParserFirstArrayAction();

    private ParserFirstArrayAction(){}

    @Override
    public IParserNode execute(ParserStackElement stack) {
        ParserArrayNode result = new ParserArrayNode();
        IParserNode node = stack.createParserNode();
        if (node != null) {
            //Null elements are not added.
            SynNode userNode = node.createUserNode();
            result.addSubNode(userNode);
        }
        return result;
    }

    @Override
    public String toString() {
        return "$$ = [$0]";
    }
}
