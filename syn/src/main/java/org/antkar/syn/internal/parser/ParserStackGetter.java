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

import org.antkar.syn.internal.Checks;

/**
 * A getter that returns values from a parser stack.
 */
public final class ParserStackGetter implements IParserGetter {
    private final int offset;

    public ParserStackGetter(int offset) {
        Checks.argument(offset >= 0);
        this.offset = offset;
    }

    @Override
    public IParserNode get(ParserStackElement stack) {
        IParserNode parserNode = stack.createParserNode();
        return parserNode;
    }

    @Override
    public int offset() {
        return offset;
    }

    @Override
    public String toString() {
        return "$" + offset;
    }
}
