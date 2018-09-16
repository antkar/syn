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
 * A parser action that adds an element to an existing array node. Used in a "recursive" production generated
 * by a repetition element.
 */
public final class ParserNextArrayAction implements IParserAction {
    public static final ParserNextArrayAction WITH_SEPARATOR = new ParserNextArrayAction(true);
    public static final ParserNextArrayAction WITHOUT_SEPARATOR = new ParserNextArrayAction(false);

    private final boolean separator;

    private ParserNextArrayAction(boolean separator) {
        this.separator = separator;
    }

    @Override
    public IParserNode execute(ParserStackElement stack) {
        //Getting an element node.
        IParserNode itemNode = stack.createParserNode();

        //Skipping the element node.
        stack = stack.getPrev();

        if (separator) {
            //Skipping a separator.
            stack = stack.getPrev();
        }

        //Getting an array node.
        IParserNode iArrayNode = stack.createParserNode();

        //Adding the element to the array.
        ParserArrayNode parserArrayNode = (ParserArrayNode) iArrayNode;
        if (itemNode != null) {
            SynNode userItemNode = itemNode.createUserNode();
            parserArrayNode.addSubNode(userItemNode);
        }

        return parserArrayNode;
    }

    @Override
    public String toString() {
        return "$$ = $" + (separator ? 2 : 1) + " + [$0]";
    }
}
