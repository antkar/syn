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
 * Result parser action. Returns the value associated with the special <code>result</code> attribute.
 */
public final class ParserResultAction implements IParserAction {
    private final IParserGetter getter;

    public ParserResultAction(IParserGetter getter) {
        this.getter = Checks.notNull(getter);
    }

    @Override
    public IParserNode execute(ParserStackElement stack) {
        int getterOfs = getter.offset();
        for (int i = 0; i < getterOfs; ++i) {
            stack = stack.getPrev();
        }
        IParserNode result = getter.get(stack);
        return result;
    }

    @Override
    public String toString() {
        return "$$ = " + getter;
    }
}
