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

/**
 * A semantic action associated with a BNF production.
 */
public interface IParserAction {
    /**
     * Executes the action and calculates the result parser tree node.
     *
     * @param stack the parser stack.
     * @return the tree node.
     */
    IParserNode execute(ParserStackElement stack);
}
