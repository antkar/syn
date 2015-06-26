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
package com.karmant.syn;

/**
 * A common superclass for integer and floating-point number scanners.
 */
abstract class AbstractNumberScanner implements IPrimitiveScanner {
    private final IPrimitiveResult intPrimitiveResult;
    private long intValue;
    
    AbstractNumberScanner() {
        intPrimitiveResult = new IntPrimitiveResult();
    }
    
    /**
     * Scans a sequence of decimal digits. Appends them to the lexical analyzer's buffer.
     * 
     * @param context the lexical analyzer context.
     * @param mandatory if <code>true</code>, an exception is thrown whenever there is no decimal digit
     * at the current position of the the input.
     */
    static void scanDecimalPrimitive(PrimitiveContext context, boolean mandatory) throws SynException {
        if (mandatory) {
            if (!isDigit(context.current)) {
                TextPos pos = context.getCurrentCharPos();
                throw new SynLexicalException(pos, "Invalid decimal literal");
            }
            context.append();
            context.next();
        }
        
        while (isDigit(context.current)) {
            context.append();
            context.next();
        }
    }

    /**
     * Scans a sequence of hexadecimal digits. Appends them to the lexical analyzer's buffer.
     * 
     * @param context the lexical analyzer context.
     * @param mandatory if <code>true</code>, an exception is thrown whenever there is no hexadecimal digit
     * at the current position of the the input.
     * 
     * @return <code>true</code> if at least one hexadecimal digit was scanned.
     */
    static boolean scanHexadecimalPrimitive(PrimitiveContext context, boolean mandatory) throws SynException {
        boolean result = false;
        if (mandatory) {
            if (!isHexDigit(context.current)) {
                TextPos pos = context.getCurrentCharPos();
                throw new SynLexicalException(pos, "Invalid hexadecimal literal");
            }
            context.append();
            context.next();
            result = true;
        }
        
        while (isHexDigit(context.current)) {
            context.append();
            context.next();
            result = true;
        }
        return result;
    }
    
    /**
     * Scans an integer literal suffix - "L" or "l".
     */
    static void scanIntegerSuffix(PrimitiveContext context) throws SynException {
        if (context.current == 'L' || context.current == 'l') {
            context.next();
        }
    }
    
    /**
     * Initializes and returns a result containing an integer value (of type <code>long</code>). 
     * @param value the value.
     * @return the result.
     */
    IPrimitiveResult intResult(long value) {
        intValue = value;
        return intPrimitiveResult;
    }

    /**
     * Checks if the passed character code denotes a decimal digit.
     */
    static boolean isDigit(int k) {
        return k >= '0' && k <= '9';
    }

    /**
     * Checks if the passed character code denotes a hexadecimal digit.
     */
    private static boolean isHexDigit(int k) {
        return (k >= '0' && k <= '9') || (k >= 'A' && k <= 'F') || (k >= 'a' && k <= 'f');
    }
    
    /**
     * Integer primitive result.
     */
    private final class IntPrimitiveResult implements IPrimitiveResult {
        IntPrimitiveResult(){}

        @Override
        public TokenDescriptor getTokenDescriptor() {
            return TokenDescriptor.INTEGER;
        }

        @Override
        public TerminalNode createTokenNode(PosBuffer pos) {
            if (intValue >= Integer.MIN_VALUE && intValue <= Integer.MAX_VALUE) {
                return new IntegerValueNode(pos, (int)intValue);
            }
            return new LongValueNode(pos, intValue);
        }
    }
}
