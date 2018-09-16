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

import java.io.PrintStream;

import org.antkar.syn.internal.CommonUtil;
import org.antkar.syn.internal.lrtables.ParserState;

/**
 * Start stack element.
 */
final class StartParserStackElement extends ParserStackElement {
    StartParserStackElement(ParserState state) {
        super(null, state);
    }

    @Override
    IParserNode createParserNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    AmbiguityNode createAmbiguityNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    void print(PrintStream out, int level) {
        CommonUtil.printIndent(out, level);
        out.println(getClass().getCanonicalName());
    }
}
