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
import org.antkar.syn.SynLexicalException;
import org.antkar.syn.TextPos;
import org.antkar.syn.internal.Checks;

/**
 * Key-characters scanner. Scans custom key-character tokens.
 */
final class KeycharScanner implements IPrimitiveScanner {
    private final KeycharTreeNode startCharState;
    private final TokenNodePrimitiveResult primitiveResult;

    KeycharScanner(ScannerConfiguration config) {
        Checks.notNull(config);
        startCharState = config.getKeycharTreeRoot();
        primitiveResult = new TokenNodePrimitiveResult(null);
    }

    @Override
    public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        KeycharTreeNode charState = startCharState.getLinkedNode((char)context.current);
        if (charState == null) {
            //The current input character does not match any token. Return null.
            return null;
        }

        //There are tokens starting with the current character. Scan the rest and choose a token.
        context.next();
        for (;;) {
            KeycharTreeNode nextState = charState.getLinkedNode((char)context.current);

            if (nextState != null) {
                //Next character is matched. Read another character and go to the next state.
                context.next();
                charState = nextState;
            } else if (charState.getToken() != null) {
                //Next character is not matched, but a token has been completed by the previous character.
                //Return that token.
                primitiveResult.setTokenDescriptor(charState.getToken());
                return primitiveResult;
            } else {
                //Next character is not matched, a token is not recognized. Lexical error.
                TextPos pos = context.getCurrentCharPos();
                throw new SynLexicalException(pos, "Lexical error");
            }
        }
    }
}
