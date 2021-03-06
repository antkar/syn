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

import org.antkar.syn.ValueNode;

/**
 * Constant getter. Returns a constant defined in a grammar.
 */
public final class ParserConstGetter implements IParserGetter {
    private final ValueNode userNode;
    private final IParserNode parserNode;

    public ParserConstGetter(ValueNode userNode) {
        this.userNode = userNode;
        this.parserNode = new ParserUserNode(userNode);
    }

    @Override
    public IParserNode get(ParserStackElement element) {
        return parserNode;
    }

    @Override
    public int offset() {
        return 0;
    }

    @Override
    public String toString() {
        return String.valueOf(userNode);
    }
}
