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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Unit tests for {@link TokenStream}: string literals.
 */
public class TokenStreamStringTest extends TestCase {
	@Test
	public void testSimpleStringLiterals() throws SynException {
		checkString("\"Hello, World!\"", "Hello, World!");
		checkString("'Hello, World!'", "Hello, World!");
	}
	
	@Test
	public void testSimpleEscapeSequences() throws SynException {
		checkString("'\\n'", "\n");
		checkString("'\\b'", "\b");
		checkString("'\\t'", "\t");
		checkString("'\\f'", "\f");
		checkString("'\\r'", "\r");

		checkString("\"\\n\"", "\n");
		checkString("\"\\b\"", "\b");
		checkString("\"\\t\"", "\t");
		checkString("\"\\f\"", "\f");
		checkString("\"\\r\"", "\r");
	}
	
	@Test
	public void testQuoteEscapeSequences() throws SynException {
		checkString("'\\'Hello\\''", "'Hello'");
		checkString("'\\\"Hello\\\"'", "\"Hello\"");
		checkString("\"\\'Hello\\'\"", "'Hello'");
		checkString("\"\\\"Hello\\\"\"", "\"Hello\"");
	}
	
	@Test
	public void testSlashEscapeSequences() throws SynException {
		checkString("'\\\\'", "\\");
		checkString("\"\\\\\"", "\\");
	}

	@Test
	public void testAsciiEscapeSequences() throws SynException {
		checkString("'\\1Hello'", "\1Hello");
		checkString("'\\12Hello'", "\12Hello");
		checkString("'\\123Hello'", "\123Hello");
		checkString("'\\1234Hello'", "\1234Hello");
		checkString("\"\\1Hello\"", "\1Hello");
		checkString("\"\\12Hello\"", "\12Hello");
		checkString("\"\\123Hello\"", "\123Hello");
		checkString("\"\\1234Hello\"", "\1234Hello");
		checkString("'\\1\\1Hello'", "\1\1Hello");
		checkString("'\\12\\12Hello'", "\12\12Hello");
		checkString("'\\123\\123Hello'", "\123\123Hello");
		checkString("'\\1234\\1234Hello'", "\1234\1234Hello");
		checkString("\"\\1\\1Hello\"", "\1\1Hello");
		checkString("\"\\12\\12Hello\"", "\12\12Hello");
		checkString("\"\\123\\123Hello\"", "\123\123Hello");
		checkString("\"\\1234\\1234Hello\"", "\1234\1234Hello");
	}
	
	@Test
	public void testUnicodeEscapeSequences() throws SynException {
		checkString("'\\u1234'", "\u1234");
		checkString("'\\u12345'", "\u12345");
		checkString("'\\u1234\\u5678'", "\u1234\u5678");
		checkString("'\\uAbCd'", "\uABCD");
		checkString("\"\\u1234\"", "\u1234");
		checkString("\"\\u12345\"", "\u12345");
		checkString("\"\\u1234\\u5678\"", "\u1234\u5678");
		checkString("\"\\uAbCd\"", "\uABCD");
	}
	
	@Test
	public void testUnicodeEscapeSequenceError() throws SynException {
		TokenStreamNumberTest.checkError("'\\u1'");
		TokenStreamNumberTest.checkError("'\\u12'");
		TokenStreamNumberTest.checkError("'\\u123'");
		TokenStreamNumberTest.checkError("'\\uwxyz'");
		TokenStreamNumberTest.checkError("'\\uabcz'");
	}

	@Test
	public void testInvalidEscapeSequence() throws SynException {
		TokenStreamNumberTest.checkError("'\\z'");
	}

	@Test
	public void testUncompletedStringLiteral() throws SynException {
		TokenStreamNumberTest.checkError("'Hello, ");
		TokenStreamNumberTest.checkError("'Hello, \nWorld!'");
		TokenStreamNumberTest.checkError("'Hello, \rWorld!'");
		TokenStreamNumberTest.checkError("\"Hello, ");
		TokenStreamNumberTest.checkError("\"Hello, \nWorld!\"");
		TokenStreamNumberTest.checkError("\"Hello, \rWorld!\"");
	}
	
	private static void checkString(String source, String value) throws SynException {
		DefaultTokenStream tokenStream = TokenStreamNumberTest.createTokenStream(source);
		TokenStreamTest.checkLiteralToken(tokenStream, TokenType.STRING);
		ValueNode node = (ValueNode) tokenStream.getTokenNode();
		assertEquals(value, node.getString());
		TokenStreamTest.checkLiteralToken(tokenStream, TokenType.END_OF_FILE);
	}
}
