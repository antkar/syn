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
package org.antkar.syn;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.antkar.syn.DefaultTokenStream;
import org.antkar.syn.ScannerConfiguration;
import org.antkar.syn.ScannerConfigurator;
import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.StringSourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.SynLexicalException;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.TokenStream;
import org.antkar.syn.TokenType;
import org.antkar.syn.ValueNode;
import org.junit.Test;

/**
 * Unit tests for {@link TokenStream}: numeric literals.
 */
public class TokenStreamNumberTest extends TestCase {
    private static final Collection<TokenDescriptor> TOKEN_DESCRIPTORS;
    
    static {
        Collection<TokenDescriptor> col = new ArrayList<>();
        col.add(TokenDescriptor.forLiteral("+"));
        col.add(TokenDescriptor.forLiteral("-"));
        col.add(TokenDescriptor.forLiteral("."));
        col.add(TokenDescriptor.forType(TokenType.FLOAT));
        TOKEN_DESCRIPTORS = col;
    }
    
    @Test
    public void testSmallIntegers() throws SynException {
        checkInteger("0", 0);
        checkInteger("0L", 0);
        checkInteger("0l", 0);
        checkInteger("00", 0);
        checkInteger("00L", 0);
        checkInteger("00l", 0);
        checkInteger("0x0", 0);
        checkInteger("0x0L", 0);
        checkInteger("0x0l", 0);
        checkInteger("12", 12);
        checkInteger("12L", 12);
        checkInteger("12l", 12);
        checkInteger("076", 076);
        checkInteger("076L", 076);
        checkInteger("076l", 076);
        checkInteger("0xabc", 0xabc);
        checkInteger("0xabcL", 0xabc);
        checkInteger("0xabcl", 0xabc);
        checkInteger("0Xabc", 0xabc);
        checkInteger("0XabcL", 0xabc);
        checkInteger("0Xabcl", 0xabc);
        checkInteger("0xABC", 0xabc);
        checkInteger("0xABCL", 0xabc);
        checkInteger("0xABCl", 0xabc);
        checkInteger("0XABC", 0xabc);
        checkInteger("0XABCL", 0xabc);
        checkInteger("0XABCl", 0xabc);
        checkInteger("0xAbC", 0xabc);
        checkInteger("0xAbCL", 0xabc);
        checkInteger("0xAbCl", 0xabc);
    }
    
    @Test
    public void testMaximalValidIntegers() throws SynException {
        checkInteger("2147483647", 2147483647);
        checkInteger("2147483647L", 2147483647);
        checkInteger("2147483647l", 2147483647);
        checkInteger("017777777777", 017777777777);
        checkInteger("017777777777L", 017777777777);
        checkInteger("017777777777l", 017777777777);
        checkInteger("0x7FFFFFFF", 0x7FFFFFFF);
        checkInteger("0x7FFFFFFFL", 0x7FFFFFFF);
        checkInteger("0x7FFFFFFFl", 0x7FFFFFFF);
        
        checkLong("9223372036854775807", 9223372036854775807L);
        checkLong("9223372036854775807L", 9223372036854775807L);
        checkLong("9223372036854775807l", 9223372036854775807L);
        checkLong("9223372036854775808", -9223372036854775808L);
        checkLong("9223372036854775808L", -9223372036854775808L);
        checkLong("9223372036854775808l", -9223372036854775808L);
        checkLong("01777777777777777777777", 01777777777777777777777L);
        checkLong("01777777777777777777777L", 01777777777777777777777L);
        checkLong("01777777777777777777777l", 01777777777777777777777L);
        checkLong("0xFFFFFFFFFFFFFFFF", 0xFFFFFFFFFFFFFFFFL);
        checkLong("0xFFFFFFFFFFFFFFFFL", 0xFFFFFFFFFFFFFFFFL);
        checkLong("0xFFFFFFFFFFFFFFFFl", 0xFFFFFFFFFFFFFFFFL);
    }

    @Test
    public void testIntegersOutOfRange() throws SynException {
        checkError("9223372036854775809");
        checkError("9223372036854775809L");
        checkError("9223372036854775809l");
        checkError("02000000000000000000000");
        checkError("02000000000000000000000L");
        checkError("02000000000000000000000l");
        checkError("0x10000000000000000");
        checkError("0x10000000000000000L");
        checkError("0x10000000000000000l");
    }
    
    @Test
    public void testFloatSuffixes() throws SynException {
        checkFloat("0f", 0.0);
        checkFloat("0F", 0.0);
        checkFloat("0d", 0.0);
        checkFloat("0D", 0.0);
        checkFloat("13f", 13.0);
        checkFloat("13F", 13.0);
        checkFloat("13d", 13.0);
        checkFloat("13D", 13.0);
    }
    
    @Test
    public void testDecimalFloatNumbers() throws SynException {
        checkFloat("123.125E5f", Double.parseDouble("123.125E5"));
        checkFloat("123.125e5f", Double.parseDouble("123.125E5"));
        checkFloat("123.125E+5f", Double.parseDouble("123.125E5"));
        checkFloat("123.125E-5f", Double.parseDouble("123.125E-5"));
        checkFloat("123.125E-5F", Double.parseDouble("123.125E-5"));
        checkFloat("123.125E-5d", Double.parseDouble("123.125E-5"));
        checkFloat("123.125E-5D", Double.parseDouble("123.125E-5"));

        checkFloat("123.125E5", Double.parseDouble("123.125E5"));
        checkFloat("123.125e5", Double.parseDouble("123.125E5"));
        checkFloat("123.125E+5", Double.parseDouble("123.125E5"));
        checkFloat("123.125E-5", Double.parseDouble("123.125E-5"));

        checkFloat("123.125f", Double.parseDouble("123.125"));
        checkFloat("123.125F", Double.parseDouble("123.125"));
        checkFloat("123.125d", Double.parseDouble("123.125"));
        checkFloat("123.125D", Double.parseDouble("123.125"));

        checkFloat("123.125", Double.parseDouble("123.125"));
        checkFloat("123.125", Double.parseDouble("123.125"));
        checkFloat("123.125", Double.parseDouble("123.125"));

        checkFloat("123.E5f", Double.parseDouble("123E5"));
        checkFloat("123.e5f", Double.parseDouble("123E5"));
        checkFloat("123.E+5f", Double.parseDouble("123E5"));
        checkFloat("123.E-5f", Double.parseDouble("123E-5"));
        checkFloat("123.E+5F", Double.parseDouble("123E5"));
        checkFloat("123.E+5d", Double.parseDouble("123E5"));
        checkFloat("123.E+5D", Double.parseDouble("123E5"));

        checkFloat("123.E5", Double.parseDouble("123E5"));
        checkFloat("123.e5", Double.parseDouble("123E5"));
        checkFloat("123.E+5", Double.parseDouble("123E5"));
        checkFloat("123.E-5", Double.parseDouble("123E-5"));

        checkFloat("123.f", Double.parseDouble("123"));
        checkFloat("123.F", Double.parseDouble("123"));
        checkFloat("123.D", Double.parseDouble("123"));
        checkFloat("123.d", Double.parseDouble("123"));

        checkFloat("123.", Double.parseDouble("123"));

        checkFloat("123E5f", Double.parseDouble("123E5"));
        checkFloat("123e5f", Double.parseDouble("123E5"));
        checkFloat("123E+5f", Double.parseDouble("123E5"));
        checkFloat("123E-5f", Double.parseDouble("123E-5"));
        checkFloat("123E+5F", Double.parseDouble("123E5"));
        checkFloat("123E+5d", Double.parseDouble("123E5"));
        checkFloat("123E+5D", Double.parseDouble("123E5"));

        checkFloat("123E5", Double.parseDouble("123E5"));
        checkFloat("123e5", Double.parseDouble("123E5"));
        checkFloat("123E+5", Double.parseDouble("123E5"));
        checkFloat("123E-5", Double.parseDouble("123E-5"));

        checkFloat(".125E5f", Double.parseDouble("0.125E5"));
        checkFloat(".125e5f", Double.parseDouble("0.125E5"));
        checkFloat(".125E+5f", Double.parseDouble("0.125E5"));
        checkFloat(".125E-5f", Double.parseDouble("0.125E-5"));
        checkFloat(".125E5F", Double.parseDouble("0.125E5"));
        checkFloat(".125E5d", Double.parseDouble("0.125E5"));
        checkFloat(".125E5D", Double.parseDouble("0.125E5"));

        checkFloat(".125E5", Double.parseDouble("0.125E5"));
        checkFloat(".125e5", Double.parseDouble("0.125E5"));
        checkFloat(".125E+5", Double.parseDouble("0.125E5"));
        checkFloat(".125E-5", Double.parseDouble("0.125E-5"));

        checkFloat(".125f", Double.parseDouble("0.125"));
        checkFloat(".125F", Double.parseDouble("0.125"));
        checkFloat(".125d", Double.parseDouble("0.125"));
        checkFloat(".125D", Double.parseDouble("0.125"));

        checkFloat(".125", Double.parseDouble("0.125"));
    }
    
    @Test
    public void testHexadecimalFloatNumbers() throws SynException {
        checkFloat("0xabc.P5f", 0xABCP5);
        checkFloat("0xabc.p5f", 0xABCP5);
        checkFloat("0xabc.P+5f", 0xABCP5);
        checkFloat("0xabc.P-5f", 0xABCP-5);
        checkFloat("0xabc.P5F", 0xABCP5);
        checkFloat("0xabc.P5d", 0xABCP5);
        checkFloat("0xabc.P5D", 0xABCP5);
        
        checkFloat("0xabc.P5", 0xABCP5);
        checkFloat("0xabc.p5", 0xABCP5);
        checkFloat("0xabc.P+5", 0xABCP5);
        checkFloat("0xabc.P-5", 0xABCP-5);
        
        checkFloat("0xabcP5f", 0xABCP5);
        checkFloat("0xabcp5f", 0xABCP5);
        checkFloat("0xabcP+5f", 0xABCP5);
        checkFloat("0xabcP-5f", 0xABCP-5);
        checkFloat("0xabcP5F", 0xABCP5);
        checkFloat("0xabcP5d", 0xABCP5);
        checkFloat("0xabcP5D", 0xABCP5);

        checkFloat("0xabcP5", 0xABCP5);
        checkFloat("0xabcp5", 0xABCP5);
        checkFloat("0xabcP+5", 0xABCP5);
        checkFloat("0xabcP-5", 0xABCP-5);
        
        checkFloat("0xabc.defP5f", 0xABC.DEFP5);
        checkFloat("0xabc.defp5f", 0xABC.DEFP5);
        checkFloat("0xabc.defP+5f", 0xABC.DEFP5);
        checkFloat("0xabc.defP-5f", 0xABC.DEFP-5);
        checkFloat("0xabc.defP5F", 0xABC.DEFP5);
        checkFloat("0xabc.defP5d", 0xABC.DEFP5);
        checkFloat("0xabc.defP5D", 0xABC.DEFP5);

        checkFloat("0xabc.defP5", 0xABC.DEFP5);
        checkFloat("0xabc.defp5", 0xABC.DEFP5);
        checkFloat("0xabc.defP+5", 0xABC.DEFP5);
        checkFloat("0xabc.defP-5", 0xABC.DEFP-5);

        checkFloat("0x.defP5f", 0x.DEFP5);
        checkFloat("0x.defp5f", 0x.DEFP5);
        checkFloat("0x.defP+5f", 0x.DEFP5);
        checkFloat("0x.defP-5f", 0x.DEFP-5);
        checkFloat("0x.defP5F", 0x.DEFP5);
        checkFloat("0x.defP5d", 0x.DEFP5);
        checkFloat("0x.defP5D", 0x.DEFP5);

        checkFloat("0x.defP5", 0x.DEFP5);
        checkFloat("0x.defp5", 0x.DEFP5);
        checkFloat("0x.defP+5", 0x.DEFP5);
        checkFloat("0x.defP-5", 0x.DEFP-5);
    }
    
    @Test
    public void testMaximalValidFloats() throws SynException {
        checkFloat("1.7976931348623157e+308", Double.parseDouble("1.7976931348623157e+308"));
        checkFloat("1.7976931348623157e+308f", Double.parseDouble("1.7976931348623157e+308"));
        checkFloat("1.7976931348623157e+308F", Double.parseDouble("1.7976931348623157e+308"));
        checkFloat("1.7976931348623157e+308d", Double.parseDouble("1.7976931348623157e+308"));
        checkFloat("1.7976931348623157e+308D", Double.parseDouble("1.7976931348623157e+308"));
        checkFloat("0x1.fffffffffffffP+1023", Double.parseDouble("0x1.fffffffffffffP+1023"));
        checkFloat("0x1.fffffffffffffP+1023f", Double.parseDouble("0x1.fffffffffffffP+1023"));
        checkFloat("0x1.fffffffffffffP+1023F", Double.parseDouble("0x1.fffffffffffffP+1023"));
        checkFloat("0x1.fffffffffffffP+1023d", Double.parseDouble("0x1.fffffffffffffP+1023"));
        checkFloat("0x1.fffffffffffffP+1023D", Double.parseDouble("0x1.fffffffffffffP+1023"));
    }
    
    @Test
    public void testFloatsOutOfRange() throws SynException {
        checkError("1.7976931348623159e+308");
        checkError("1.7976931348623159e+308f");
        checkError("1.7976931348623159e+308F");
        checkError("1.7976931348623159e+308d");
        checkError("1.7976931348623159e+308D");
        checkError("0x2P+1023");
        checkError("0x2P+1023f");
        checkError("0x2P+1023F");
        checkError("0x2P+1023d");
        checkError("0x2P+1023D");
    }
    
    @Test
    public void testDotResolving() throws SynException {
        DefaultTokenStream ts = createTokenStream(". 123");
        checkToken(ts, TokenType.KEYCHAR);
        assertEquals(".", ts.getTokenDescriptor().getLiteral());
        checkToken(ts, TokenType.INTEGER);
        assertEquals(123, ((ValueNode) ts.getTokenNode()).getInt());

        ts = createTokenStream("123 .");
        checkToken(ts, TokenType.INTEGER);
        assertEquals(123, ((ValueNode) ts.getTokenNode()).getInt());
        checkToken(ts, TokenType.KEYCHAR);
        assertEquals(".", ts.getTokenDescriptor().getLiteral());

        ts = createTokenStream("123..");
        checkToken(ts, TokenType.FLOAT);
        assertEquals(123.0, ((ValueNode) ts.getTokenNode()).getFloat());
        checkToken(ts, TokenType.KEYCHAR);
        assertEquals(".", ts.getTokenDescriptor().getLiteral());
    }

    private static void checkInteger(String source, int value) throws SynException {
        DefaultTokenStream tokenStream = createTokenStream(source);
        checkToken(tokenStream, TokenType.INTEGER);
        ValueNode node = (ValueNode) tokenStream.getTokenNode();
        assertEquals(value, node.getInt());
        checkToken(tokenStream, TokenType.END_OF_FILE);
    }

    private static void checkLong(String source, long value) throws SynException {
        DefaultTokenStream tokenStream = createTokenStream(source);
        checkToken(tokenStream, TokenType.INTEGER);
        ValueNode node = (ValueNode) tokenStream.getTokenNode();
        assertEquals(value, node.getLong());
        checkToken(tokenStream, TokenType.END_OF_FILE);
    }

    private static void checkFloat(String source, double value) throws SynException {
        DefaultTokenStream tokenStream = createTokenStream(source);
        checkToken(tokenStream, TokenType.FLOAT);
        ValueNode node = (ValueNode) tokenStream.getTokenNode();
        assertEquals(value, node.getFloat());
        checkToken(tokenStream, TokenType.END_OF_FILE);
    }

    private static void checkToken(DefaultTokenStream tokenStream, TokenType type) throws SynException {
        TokenStreamTest.checkLiteralToken(tokenStream, type);
    }
    
    static void checkError(String source) throws SynException {
        DefaultTokenStream tokenStream = createTokenStream(source);
        try {
            tokenStream.nextToken();
            fail();
        } catch (SynLexicalException e) {
            // ok
        }
    }

    static DefaultTokenStream createTokenStream(String source) throws SynException {
        SourceDescriptor sourceDescriptor = new StringSourceDescriptor("<input>");
        ScannerConfiguration scannerConfiguration = 
            ScannerConfigurator.makeConfiguration(TOKEN_DESCRIPTORS);
        Reader reader = new StringReader(source);
        DefaultTokenStream tokenStream = new DefaultTokenStream(sourceDescriptor, scannerConfiguration, reader);
        return tokenStream;
    }
}
