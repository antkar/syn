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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.antkar.syn.DefaultTokenStream;
import org.antkar.syn.ScannerConfiguration;
import org.antkar.syn.ScannerConfigurator;
import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.StringSourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.TextPos;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.TokenStream;
import org.antkar.syn.TokenType;
import org.junit.Test;

/**
 * Unit tests for {@link TokenStream}.
 */
public class TokenStreamTest extends TestCase {
    private static final Collection<TokenDescriptor> TOKEN_DESCRIPTORS;
    
    static {
        Collection<TokenDescriptor> col = new ArrayList<>();
        col.add(TokenDescriptor.END_OF_FILE);
        col.add(TokenDescriptor.FLOAT);
        col.add(TokenDescriptor.ID);
        col.add(TokenDescriptor.INTEGER);
        col.add(TokenDescriptor.STRING);
        col.add(TokenDescriptor.forLiteral("if"));
        col.add(TokenDescriptor.forLiteral("else"));
        col.add(TokenDescriptor.forLiteral("while"));
        col.add(TokenDescriptor.forLiteral("for"));
        col.add(TokenDescriptor.forLiteral("break"));
        col.add(TokenDescriptor.forLiteral("continue"));
        col.add(TokenDescriptor.forLiteral(";"));
        col.add(TokenDescriptor.forLiteral("?"));
        col.add(TokenDescriptor.forLiteral(":"));
        col.add(TokenDescriptor.forLiteral(","));
        col.add(TokenDescriptor.forLiteral("."));
        col.add(TokenDescriptor.forLiteral("+"));
        col.add(TokenDescriptor.forLiteral("-"));
        col.add(TokenDescriptor.forLiteral("/"));
        col.add(TokenDescriptor.forLiteral("*"));
        col.add(TokenDescriptor.forLiteral("="));
        col.add(TokenDescriptor.forLiteral("=="));
        col.add(TokenDescriptor.forLiteral("!="));
        col.add(TokenDescriptor.forLiteral("<="));
        col.add(TokenDescriptor.forLiteral(">="));
        col.add(TokenDescriptor.forLiteral("<"));
        col.add(TokenDescriptor.forLiteral(">"));
        col.add(TokenDescriptor.forLiteral(">>"));
        col.add(TokenDescriptor.forLiteral("<<"));
        col.add(TokenDescriptor.forLiteral(">>>"));
        col.add(TokenDescriptor.forLiteral("!"));
        col.add(TokenDescriptor.forLiteral("&="));
        col.add(TokenDescriptor.forLiteral("&"));
        col.add(TokenDescriptor.forLiteral("&&"));
        col.add(TokenDescriptor.forLiteral("&&="));
        col.add(TokenDescriptor.forLiteral("+="));
        col.add(TokenDescriptor.forLiteral("-="));
        col.add(TokenDescriptor.forLiteral("++"));
        col.add(TokenDescriptor.forLiteral("--"));
        col.add(TokenDescriptor.forLiteral("{"));
        col.add(TokenDescriptor.forLiteral("}"));
        col.add(TokenDescriptor.forLiteral("("));
        col.add(TokenDescriptor.forLiteral(")"));
        col.add(TokenDescriptor.forLiteral("["));
        col.add(TokenDescriptor.forLiteral("]"));
        TOKEN_DESCRIPTORS = col;
    }

    @Test
    public void testTokenTypeLiteral() throws Exception {
        ScannerConfiguration config = ScannerConfigurator.makeConfiguration(TOKEN_DESCRIPTORS);
        SourceDescriptor sourceDescriptor = new StringSourceDescriptor("<input>");
        try (
                InputStream in = getClass().getResourceAsStream("TokenStreamTest_TokenTypeLiteral.txt");
                Reader reader = new InputStreamReader(in))
        {
            DefaultTokenStream tokenStream = new DefaultTokenStream(sourceDescriptor, config, reader);
            checkLiteralToken(tokenStream, TokenType.FLOAT);
            checkLiteralToken(tokenStream, TokenType.ID);
            checkLiteralToken(tokenStream, TokenType.INTEGER);
            checkLiteralToken(tokenStream, TokenType.STRING);
            tokenStream.nextToken();
            checkKeyword(tokenStream, "if");
            tokenStream.nextToken();
            checkKeyword(tokenStream, "else");
            tokenStream.nextToken();
            checkKeyword(tokenStream, "while");
            tokenStream.nextToken();
            checkKeyword(tokenStream, "for");
            tokenStream.nextToken();
            checkKeyword(tokenStream, "break");
            tokenStream.nextToken();
            checkKeyword(tokenStream, "continue");
            checkKeychar(tokenStream, ";");
            checkKeychar(tokenStream, "?");
            checkKeychar(tokenStream, ":");
            checkKeychar(tokenStream, ",");
            checkKeychar(tokenStream, ".");
            checkKeychar(tokenStream, "+");
            checkKeychar(tokenStream, "-");
            checkKeychar(tokenStream, "/");
            checkKeychar(tokenStream, "*");
            checkKeychar(tokenStream, "=");
            checkKeychar(tokenStream, "==");
            checkKeychar(tokenStream, "!=");
            checkKeychar(tokenStream, "<=");
            checkKeychar(tokenStream, ">=");
            checkKeychar(tokenStream, "<");
            checkKeychar(tokenStream, ">");
            checkKeychar(tokenStream, ">>");
            checkKeychar(tokenStream, "<<");
            checkKeychar(tokenStream, ">>>");
            checkKeychar(tokenStream, "!");
            checkKeychar(tokenStream, "&=");
            checkKeychar(tokenStream, "&");
            checkKeychar(tokenStream, "&&");
            checkKeychar(tokenStream, "&&=");
            checkKeychar(tokenStream, "+=");
            checkKeychar(tokenStream, "-=");
            checkKeychar(tokenStream, "++");
            checkKeychar(tokenStream, "--");
            checkKeychar(tokenStream, "{");
            checkKeychar(tokenStream, "}");
            checkKeychar(tokenStream, "(");
            checkKeychar(tokenStream, ")");
            checkKeychar(tokenStream, "[");
            checkKeychar(tokenStream, "]");
            checkLiteralToken(tokenStream, TokenType.END_OF_FILE);
            checkLiteralToken(tokenStream, TokenType.END_OF_FILE);
        }
    }
    
    @Test
    public void testTokenPos() throws Exception
    {
        ScannerConfiguration config = ScannerConfigurator.makeConfiguration(TOKEN_DESCRIPTORS);
        SourceDescriptor sourceDescriptor = new StringSourceDescriptor("<input>");
        try (
                InputStream in = getClass().getResourceAsStream("TokenStreamTest_TokenPos.txt");
                Reader reader = new InputStreamReader(in))
        {
            DefaultTokenStream tokenStream = new DefaultTokenStream(sourceDescriptor, config, reader);
            checkTokenPos(tokenStream, sourceDescriptor, 1, 3, 5);
            checkTokenPos(tokenStream, sourceDescriptor, 1, 9, 3);
            checkTokenPos(tokenStream, sourceDescriptor, 1, 14, 10);
            checkTokenPos(tokenStream, sourceDescriptor, 2, 5, 7);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 9, 2);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 12, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 13, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 14, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 16, 8);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 24, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 26, 4);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 31, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 32, 5);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 37, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 3, 38, 1);
            checkTokenPos(tokenStream, sourceDescriptor, 4, 1, 2);
            checkTokenPos(tokenStream, sourceDescriptor, 4, 4, 2);
            checkTokenPos(tokenStream, sourceDescriptor, 4, 8, 2);
            checkTokenPos(tokenStream, sourceDescriptor, 4, 11, 2);
            checkTokenPos(tokenStream, sourceDescriptor, 5, 7, 0);
            checkTokenPos(tokenStream, sourceDescriptor, 5, 7, 0);
        }
    }

    static void checkLiteralToken(DefaultTokenStream ts, TokenType tokenType) throws SynException {
        ts.nextToken();
        TokenType type = ts.getTokenDescriptor().getType();
        assertEquals(tokenType, type);
    }
    
    private static void checkKeyword(DefaultTokenStream ts, String literal) {
        TokenType type = ts.getTokenDescriptor().getType();
        assertEquals(TokenType.KEYWORD, type);
        String actualLiteral = ts.getTokenDescriptor().getLiteral();
        assertEquals(literal, actualLiteral);
    }

    private static void checkKeychar(DefaultTokenStream ts, String literal) throws SynException {
        checkLiteralToken(ts, TokenType.KEYCHAR);
        String actualLiteral = ts.getTokenDescriptor().getLiteral();
        assertEquals(literal, actualLiteral);
    }
    
    private static void checkTokenPos(
            DefaultTokenStream ts,
            SourceDescriptor sourceDescriptor,
            int line,
            int column,
            int length) throws SynException 
    {
        ts.nextToken();
        TextPos pos = ts.getTokenPos();
        assertEquals(sourceDescriptor, pos.getSource());
        assertEquals(line, pos.getLine());
        assertEquals(column, pos.getColumn());
        assertEquals(length, pos.getLength());
    }
}
