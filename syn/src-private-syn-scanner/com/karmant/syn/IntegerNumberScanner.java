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
 * Integer literal scanner. Scans a decimal, hexadecimal or octal integer literal. 
 */
class IntegerNumberScanner extends AbstractNumberScanner {

	/** Maximum decimal integer literal. Used to check a value range. */
	private static final String MAX_DEC_LITERAL = Long.toString(Long.MIN_VALUE).substring(1);
	
	/** The length of the maximum decimal literal in characters. */
	private static final int MAX_DEC_LITERAL_LEN = MAX_DEC_LITERAL.length();

	IntegerNumberScanner() {
		super();
	}
	
	@Override
	public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
		IPrimitiveResult result = null;
		
		if (context.current == '0') {
			context.setMaxBufferLength(NumberScanner.MAX_NUMERIC_LITERAL_LENGTH);
			context.next();
			if (context.current == 'x' || context.current == 'X') {
				//The literal starts with '0x' - hexadecimal.
				context.next();
				result = scanHexNumber(context);
			} else {
				//Starts with '0' - octal.
				context.append('0');
				//Scan the number as a decimal. The radix will be taken into account later, when the
				//string representation is converted to a number.
				result = scanDecNumber(context);
			}
		} else if (AbstractNumberScanner.isDigit(context.current)) {
			//Decimal literal.
			context.setMaxBufferLength(NumberScanner.MAX_NUMERIC_LITERAL_LENGTH);
			result = scanDecNumber(context);
		}
		
		return result;
	}

	/**
	 * Scans a hexadecimal literal.
	 */
	private IPrimitiveResult scanHexNumber(PrimitiveContext context) throws SynException {
		//Scan digits.
		boolean scanned = AbstractNumberScanner.scanHexadecimalPrimitive(context, false);
		if (scanned) {
			//Scan optional suffix.
			AbstractNumberScanner.scanIntegerSuffix(context);
		} else {
			//No digits after '0x'. Error.
			TextPos pos = context.getCurrentCharPos();
			throw new SynLexicalException(pos, "Invalid hexadecimal literal");
		}

		//Convert the string to a number.
		StringBuilder bld = context.getStringBuilder();
		long value = strToIntHex(context, bld, 0);
		return intResult(value);
	}

	/**
	 * Scans a decimal or an octal number.
	 */
	private IPrimitiveResult scanDecNumber(PrimitiveContext context) throws SynException {
		//Scan digits.
		AbstractNumberScanner.scanDecimalPrimitive(context, false);
		//Scan optional suffix.
		AbstractNumberScanner.scanIntegerSuffix(context);
		
		//Convert to a number.
		long value = strToInt(context);
		return intResult(value);
	}

	/**
	 * Converts a string decimal or octal representation of a number into a numeric value. The string
	 * representation is taken from the scanner state's buffer.
	 */
	static long strToInt(PrimitiveContext context) throws SynException {
		long value;
		StringBuilder bld = context.getStringBuilder();
		if (bld.charAt(0) == '0') {
			//Octal number.
			value = strToIntOct(context, bld);
		} else {
			//Decimal.
			value = strToIntDec(context, bld);
		}
		return value;
	}

	/**
	 * Converts a string octal representation of a number into a numeric value.
	 * Differs from {@link Long#parseLong(String, int) Long.parseLong(s, 8)} by not considering 2^63
	 * out of range.
	 */
	private static long strToIntOct(PrimitiveContext context, StringBuilder bld) throws SynException {
		int ofs = skipLeadingZeros(bld, 0);
		verifyOctRange(context, bld, ofs);

		long result = 0;
		
		//Convert the value, digit by digit.
		int len = bld.length();
		while (ofs < len) {
			char c = bld.charAt(ofs);
			if (!(c >= '0' && c <= '7')) {
				TextPos pos = context.getCurrentTokenPos();
				throw new SynLexicalException(pos, "Invalid octal literal");
			}
			
			int d = c - '0';
			result = (result << 3) | d;
			++ofs;
		}
		
		return result;
	}
	
	/**
	 * Checks whether a string octal representation of a number is out of range.
	 * If the value is out if range, an exception is thrown. 
	 */
	private static void verifyOctRange(PrimitiveContext context, StringBuilder bld, int ofs) throws SynLexicalException {
		int size = bld.length();
		int len = size - ofs;
		
		//Maximum octal number length is 22 characters (64/3 = 21.33333333, 22*3 = 66).
		//If the literal is 22 characters long, the highest digit must be 0 or 1.
		if (len > 22 || (len == 22 && bld.charAt(ofs) > '1')) {
			TextPos pos = context.getCurrentTokenPos();
			throw new SynLexicalException(pos, "Octal value is out of range: " + bld);
		}
	}

	/**
	 * Converts a string hexadecimal representation of an integer number into a numeric value.
	 * Differs from {@link Long#parseLong(String, int) Long.parseLong(s, 16)} by not considering 2^63
	 * out of range.
	 */
	static long strToIntHex(PrimitiveContext context, StringBuilder bld, int ofs) throws SynException {
		//Check the range.
		ofs = skipLeadingZeros(bld, ofs);
		verifyHexRange(context, bld, ofs);
		
		long result = 0;
		
		//Convert digit by digit.
		int len = bld.length();
		while (ofs < len) {
			char c = bld.charAt(ofs);
			int d;
			if (c >= '0' && c <= '9') {
				d = c - '0';
			} else {
				if (c >= 'A' && c <= 'F') {
					d = 10 + (c - 'A');
				} else {
					d = 10 + (c - 'a');
				}
			}
			result = (result << 4) | d;
			++ofs;
		}
		
		return result;
	}

	/**
	 * Checks whether a string hexadecimal representation of a number is out of range.
	 * If the value is out if range, an exception is thrown. 
	 */
	private static void verifyHexRange(PrimitiveContext context, StringBuilder bld, int ofs) throws SynLexicalException {
		int size = bld.length();
		int len = size - ofs;
		
		//Maximum length of a hexadecimal literal is 16 characters (64/4 = 16).
		if (len > 16) {
			TextPos pos = context.getCurrentTokenPos();
			throw new SynLexicalException(pos, "Hexadecimal value is out of range: " + bld);
		}
	}

	/**
	 * Converts a string decimal representation of an integer number into a numeric value.
	 * Differs from {@link Long#parseLong(String)} by not considering 2^63 out of range.
	 */
	static long strToIntDec(PrimitiveContext context, StringBuilder bld) throws SynException {
		verifyDecRange(context, bld);
		
		long result = 0;
		
		int len = bld.length();
		for (int ofs = 0; ofs < len; ++ofs) {
			char c = bld.charAt(ofs);
			int d = c - '0';
			result = result * 10 + d;
		}
		
		return result;
	}
	
	/**
	 * Checks whether a string decimal representation of a number is out of range.
	 * If the value is out if range, an exception is thrown. 
	 */
	private static void verifyDecRange(PrimitiveContext context, StringBuilder bld) throws SynLexicalException {
		if (isDecOutOfRange(bld)) {
			TextPos pos = context.getCurrentTokenPos();
			throw new SynLexicalException(pos, "Decimal value is out of range: " + bld);
		}
	}
	
	private static boolean isDecOutOfRange(StringBuilder bld) {
		int len = bld.length();
		if (len < MAX_DEC_LITERAL_LEN) {
			return false;
		} else if (len > MAX_DEC_LITERAL_LEN) {
			return true;
		} else {
			//The length of the string is the same as the length of the maximum decimal number. Compare
			//the string with the maximum number's decimal representation character by character.
			for (int ofs = 0; ofs < len; ++ofs) {
				char c = bld.charAt(ofs);
				char maxc = MAX_DEC_LITERAL.charAt(ofs);
				if (c < maxc) {
					return false;
				} else if (c > maxc) {
					return true;
				}
			}
			return false;
		}
	}
	
	private static int skipLeadingZeros(StringBuilder bld, int ofs) {
		int len = bld.length();
		while (ofs < len - 1 && bld.charAt(ofs) == '0') {
			++ofs;
		}
		return ofs;
	}
}
