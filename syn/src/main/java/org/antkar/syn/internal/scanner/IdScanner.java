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
package org.antkar.syn.internal.scanner;

import org.antkar.syn.SynException;
import org.antkar.syn.TerminalNode;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.TokenType;
import org.antkar.syn.internal.IdentifierValueNode;
import org.antkar.syn.internal.PosBuffer;

/**
 * Identifier scanner.
 */
abstract class IdScanner implements IPrimitiveScanner {
    private static final int MAX_ID_LENGTH = 256;

    private final IPrimitiveResult primitiveResult;
    private String value;

    IdScanner() {
        primitiveResult = new IdPrimitiveResult();
    }

    @Override
    public final IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        if (!Character.isJavaIdentifierStart(context.current)) {
            //The current input character is not an identifier start. Return null.
            return null;
        }

        context.setMaxBufferLength(MAX_ID_LENGTH);

        //Append the first character to the buffer and read the next one.
        context.append();
        context.next();

        //Scan the rest of characters.
        while (context.current != -1 && Character.isJavaIdentifierPart(context.current)) {
            context.append();
            context.next();
        }

        //Return the result.
        value = context.getString();
        return getResult(value);
    }

    /**
     * Returns a scanner result for the specified identifier literal. Overridden by {@link KeywordScanner} to
     * translate identifiers into keywords.
     *
     * @param literal the identifier literal.
     * @return the scanner result.
     */
    IPrimitiveResult getResult(String literal) {
        return primitiveResult;
    }

    private final class IdPrimitiveResult implements IPrimitiveResult {
        IdPrimitiveResult(){}

        @Override
        public TokenDescriptor getTokenDescriptor() {
            return TokenDescriptor.forType(TokenType.ID);
        }

        @Override
        public TerminalNode createTokenNode(PosBuffer pos) {
            TerminalNode result = new IdentifierValueNode(pos, value);
            return result;
        }
    }
}
