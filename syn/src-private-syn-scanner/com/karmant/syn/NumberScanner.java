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
 * Floating-point number scanner. Supports decimal and hexadecimal numbers.
 */
class NumberScanner extends AbstractNumberScanner {
    /**
     * The maximum length of a numeric literal in characters, regardless of a radix. If a literal is longer
     * than this value, an exception is thrown in order to prevent out of memory error in case if there is a
     * very long sequence of digits in the input. 
     */
    static final int MAX_NUMERIC_LITERAL_LENGTH = 64;
    
    private final IPrimitiveResult floatPrimitiveResult;
    private double floatValue;
    
    NumberScanner() {
        super();
        floatPrimitiveResult = new FloatPrimitiveResult();
    }
    
    @Override
    public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        IPrimitiveResult result = null;
        
        //Examine the first character of the input.
        if (context.current == '0') {
            context.setMaxBufferLength(MAX_NUMERIC_LITERAL_LENGTH);
            context.next();
            if (context.current == 'x' || context.current == 'X') {
                //Hexadecimal literal.
                context.append('0');
                context.append('x');
                context.next();
                result = scanHexNumber(context);
            } else {
                //Decimal literal.
                context.append('0');
                result = scanDecNumber(context);
            }
        } else if (AbstractNumberScanner.isDigit(context.current)) {
            //A decimal digit, but not '0'. Decimal literal.
            context.setMaxBufferLength(MAX_NUMERIC_LITERAL_LENGTH);
            result = scanDecNumber(context);
        } else if (context.current == '.' && AbstractNumberScanner.isDigit(context.lookahead())) {
            //A period followed by a decimal digit.
            context.setMaxBufferLength(MAX_NUMERIC_LITERAL_LENGTH);
            context.next();
            context.append('.');
            result = scanFracDecNumber(context);
        }
        
        return result;
    }

    /**
     * Scans a hexadecimal floating-point number. 
     */
    private IPrimitiveResult scanHexNumber(PrimitiveContext context) throws SynException {
        boolean floatingPoint;
        
        //Scan an optional integer part.
        boolean intPart = AbstractNumberScanner.scanHexadecimalPrimitive(context, false);
        if (intPart) {
            if (context.current == '.') {
                context.append();
                context.next();
                AbstractNumberScanner.scanHexadecimalPrimitive(context, false);
                scanHexadecimalExponent(context, true);
                scanFloatingPointSuffix(context);
                floatingPoint = true;
            } else if (scanHexadecimalExponent(context, false)) {
                scanFloatingPointSuffix(context);
                floatingPoint = true;
            } else if (scanFloatingPointSuffix(context)) {
                floatingPoint = true;
            } else {
                AbstractNumberScanner.scanIntegerSuffix(context);
                floatingPoint = false;
            }
        } else if (context.current == '.') {
            //Scan a fractional part.
            context.append();
            context.next();
            AbstractNumberScanner.scanHexadecimalPrimitive(context, true);
            scanHexadecimalExponent(context, true);
            scanFloatingPointSuffix(context);
            floatingPoint = true;
        } else {
            //There is neither an integer part, nor a fractional one. Error.
            TextPos pos = context.getCurrentCharPos();
            throw new SynLexicalException(pos, "Invalid hexadecimal literal");
        }

        //Return the result.
        IPrimitiveResult result;
        if (floatingPoint) {
            String s = context.getString();
            floatValue = strToFloat(context, s);
            result = floatPrimitiveResult;
        } else {
            StringBuilder bld = context.getStringBuilder();
            long value = IntegerNumberScanner.strToIntHex(context, bld, 2);
            result = intResult(value);
        }
        
        return result;
    }

    /**
     * Scans a decimal floating-point number.
     */
    private IPrimitiveResult scanDecNumber(PrimitiveContext context) throws SynException {
        boolean floatingPoint = false;
        
        //Scan the number.
        
        AbstractNumberScanner.scanDecimalPrimitive(context, false);
        if (context.current == '.') {
            context.append();
            context.next();
            AbstractNumberScanner.scanDecimalPrimitive(context, false);
            floatingPoint = true;
        }
        if (scanDecimalExponent(context, false)) {
            floatingPoint = true;
        }
        if (scanFloatingPointSuffix(context)) {
            floatingPoint = true;
        }
        if (!floatingPoint) {
            AbstractNumberScanner.scanIntegerSuffix(context);
        }
        
        //Return the result.
        
        IPrimitiveResult result;
        if (floatingPoint) {
            floatValue = strToFloat(context, context.getString());
            result = floatPrimitiveResult;
        } else {
            long value = IntegerNumberScanner.strToInt(context);
            result = intResult(value);
        }
        
        return result;
    }
    
    /**
     * Scans a fractional part of a decimal number.
     */
    private IPrimitiveResult scanFracDecNumber(PrimitiveContext context) throws SynException {
        AbstractNumberScanner.scanDecimalPrimitive(context, true);
        scanDecimalExponent(context, false);
        scanFloatingPointSuffix(context);
        
        floatValue = strToFloat(context, context.getString());
        return floatPrimitiveResult;
    }
    
    /**
     * Scans a hexadecimal exponent part, e. g. <code>P+12</code>.
     */
    private static boolean scanHexadecimalExponent(PrimitiveContext context, boolean mandatory) throws SynException {
        boolean result = false;
        if (context.current == 'P' || context.current == 'p') {
            context.append();
            context.next();
            if (context.current == '+' || context.current == '-') {
                context.append();
                context.next();
            }
            AbstractNumberScanner.scanDecimalPrimitive(context, true);
            result = true;
        } else if (mandatory) {
            TextPos pos = context.getCurrentCharPos();
            throw new SynLexicalException(pos, "Hexadecimal exponent missing");
        }
        return result;
    }

    /**
     * Scans a decimal exponent part, e. g. <code>E5</code>, <code>e-10</code>.
     */
    private static boolean scanDecimalExponent(PrimitiveContext context, boolean mandatory) throws SynException {
        boolean result = false;
        if (context.current == 'E' || context.current == 'e') {
            context.append();
            context.next();
            if (context.current == '+' || context.current == '-') {
                context.append();
                context.next();
            }
            AbstractNumberScanner.scanDecimalPrimitive(context, true);
            result = true;
        } else if (mandatory) {
            TextPos pos = context.getCurrentCharPos();
            throw new SynLexicalException(pos, "Decimal exponent missing");
        }
        return result;
    }

    /**
     * Scans a floating-point suffix, e. g. <code>f</code> or <code>d</code>.
     */
    private static boolean scanFloatingPointSuffix(PrimitiveContext context) throws SynException {
        boolean result = false;
        if (context.current == 'F' || context.current == 'f' || context.current == 'D' || context.current == 'd') {
            context.next();
            result = true;
        }
        return result;
    }
    
    /**
     * Converts a string representation of a floating-point number to a numeric value.
     */
    private static double strToFloat(PrimitiveContext context, String s) throws SynException {
        try {
            double result = Double.parseDouble(s);
            if (!Double.isInfinite(result) && !Double.isNaN(result)) {
                return result;
            }
        } catch (NumberFormatException e) {
            //Do nothing here.
        }

        //Failed to parse the number. Error.
        TextPos pos = context.getCurrentTokenPos();
        throw new SynLexicalException(pos, "Floating-point value out of range: " + s);
    }
    
    private final class FloatPrimitiveResult implements IPrimitiveResult {
        FloatPrimitiveResult(){}

        @Override
        public TokenDescriptor getTokenDescriptor() {
            return TokenDescriptor.FLOAT;
        }

        @Override
        public TerminalNode createTokenNode(PosBuffer pos) {
            return new FloatValueNode(pos, floatValue);
        }
    }
}
