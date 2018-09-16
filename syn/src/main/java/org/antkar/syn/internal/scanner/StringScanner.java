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
import org.antkar.syn.TerminalNode;
import org.antkar.syn.TextPos;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.PosBuffer;
import org.antkar.syn.internal.StringValueNode;

/**
 * String literal scanner. Supports string literals enclosed in single and double quotes.
 * Allows the same escape sequences as Java 6 string literals.
 */
class StringScanner implements IPrimitiveScanner {
    /**
     * The supported maximum length of a string literal. Used to avoid out of memory errors.
     */
    private static final int MAX_STRING_LENGTH = 1 << 16;
    
    private final IPrimitiveResult primitiveResult;
    private String value;
    
    StringScanner() {
        primitiveResult = new StringPrimitiveResult();
    }
    
    @Override
    public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        if (context.current != '\'' && context.current != '"') {
            //Not a string literal. Return.
            return null;
        }
        
        //Out of memory error protection.
        context.setMaxBufferLength(MAX_STRING_LENGTH);
        
        //Scan quoted characters.
        int quote = context.current;
        context.next();
        while (context.current != quote
                && context.current != -1
                && context.current != '\n'
                && context.current != '\r')
        {
            if (context.current == '\\') {
                context.next();
                scanEscapeSequence(context);
            } else {
                context.append();
                context.next();
            }
        }
        
        if (context.current != quote) {
            //No closing quote.
            String message = context.current == -1
                    ? "String literal is not closed before the end of the file"
                    : "String literal is not closed before the end of the line";
            
            TextPos pos = context.getCurrentCharPos();
            throw new SynLexicalException(pos, message);
        }
        
        //Skip the closing quote.
        context.next();

        value = context.getString();
        return primitiveResult;
    }

    /**
     * Scans an escape sequence.
     */
    private static void scanEscapeSequence(PrimitiveContext context) throws SynException {
        int current = context.current;
        if (current == '"' || current == '\'' || current == '\\') {
            context.append();
            context.next();
        } else if (current == 'n') {
            context.append('\n');
            context.next();
        } else if (current == 't') {
            context.append('\t');
            context.next();
        } else if (current == 'b') {
            context.append('\b');
            context.next();
        } else if (current == 'r') {
            context.append('\r');
            context.next();
        } else if (current == 'f') {
            context.append('\f');
            context.next();
        } else if (current == 'u') {
            context.next();
            scanUnicode(context);
        } else if (current >= '0' && current <= '7') {
            scanOctal(context);
        } else {
            TextPos pos = context.getCurrentCharPos();
            throw new SynLexicalException(pos, "Invalid escape sequence");
        }
    }
    
    /**
     * Scans a Unicode escape sequence.
     */
    private static void scanUnicode(PrimitiveContext context) throws SynException {
        int value = 0;
        
        for (int i = 0; i < 4; ++i) {
            int d = Character.digit(context.current, 16);
            if (d == -1) {
                TextPos pos = context.getCurrentCharPos();
                throw new SynLexicalException(pos, "Invalid hexadecimal value");
            }
            value = (value << 4) | d;
            context.next();
        }
        
        context.append((char) value);
    }
    
    /**
     * Scans an octal escape sequence.
     */
    private static void scanOctal(PrimitiveContext context) throws SynException {
        int d0 = context.current - '0';
        int value = d0;
        context.next();
        
        if (context.current >= '0' && context.current <= '7') {
            value = (value << 3) | (context.current - '0');
            context.next();
            
            if (d0 <= 3 && context.current >= '0' && context.current <= '7') {
                value = (value << 3) | (context.current - '0');
                context.next();
            }
        }
        
        context.append((char) value);
    }
    
    private final class StringPrimitiveResult implements IPrimitiveResult {
        StringPrimitiveResult(){}
        
        @Override
        public TokenDescriptor getTokenDescriptor() {
            return TokenDescriptor.STRING;
        }
        
        @Override
        public TerminalNode createTokenNode(PosBuffer pos) {
            TerminalNode result = new StringValueNode(pos, value);
            return result;
        }
    }
}
