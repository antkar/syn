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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.antkar.syn.internal.bnf.BnfGrammar;
import org.antkar.syn.internal.ebnf.EbnfGrammar;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;
import org.antkar.syn.internal.grammar.XmlGrammarParser;
import org.antkar.syn.internal.lrtables.ParserConfiguration;
import org.antkar.syn.internal.lrtables.ParserConfigurator;
import org.antkar.syn.internal.lrtables.ParserState;
import org.antkar.syn.internal.parser.ParserEngine;
import org.antkar.syn.internal.scanner.DefaultTokenStream;
import org.antkar.syn.internal.scanner.ScannerConfiguration;
import org.antkar.syn.internal.scanner.ScannerConfigurator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ParserEngine}.
 */
public class ParserEngineTest extends Assert {

    @Test
    public void testSuccess() throws Exception {
        parseText("ParserEngineTest_Success.txt");
    }

    @Test
    public void testUnexpectedEof() throws Exception {
        try {
            parseText("ParserEngineTest_UnexpectedEof.txt");
            fail();
        } catch (SynSyntaxException e) {
            assertEquals(TokenDescriptor.forType(TokenType.END_OF_FILE),
                    e.getActualToken().getTokenDescriptor());
            assertEquals(6, e.getExpectedTokens().size());
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("<")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("(")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("|")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral(";")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forType(TokenType.ID)));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forType(TokenType.STRING)));
        }
    }

    @Test
    public void testSyntaxError() throws Exception {
        try {
            parseText("ParserEngineTest_SyntaxError.txt");
            fail();
        } catch (SynSyntaxException e) {
            assertEquals(TokenDescriptor.forLiteral("@"), e.getActualToken().getTokenDescriptor());
            assertEquals(1, e.getExpectedTokens().size());
            assertEquals(TokenDescriptor.forLiteral(":"), e.getExpectedTokens().get(0));
        }
    }

    @Test
    public void testSyntaxTree() throws Exception {
        SynNode tree = parseText("ParserEngineTest_Success.txt");
        ArrayNode arrayNode = (ArrayNode) tree;
        assertEquals(13, arrayNode.size());

        SynNode arrayElement = arrayNode.get(0);
        ObjectNode objectNode = (ObjectNode) arrayElement;

        ValueNode nameNode = (ValueNode) objectNode.get("name");
        assertNotNull(nameNode);
        assertEquals("Nonterminal", nameNode.getString());

        ValueNode startNode = (ValueNode) objectNode.get("start");
        assertNull(startNode);

        ArrayNode rulesNode = (ArrayNode) objectNode.get("rules");
        assertNotNull(rulesNode);
        assertEquals(1, rulesNode.size());
    }

    private SynNode parseText(String fileName) throws Exception {
        URL url = XmlGrammarParser.class.getResource("syn_grammar.xml");
        EbnfGrammar eGrammar = XmlGrammarParser.parseGrammar(url);
        BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);
        ParserConfiguration parserConfig = ParserConfigurator.makeConfiguration(bGrammar);
        ScannerConfiguration scannerConfig = ScannerConfigurator.makeConfiguration(bGrammar.getTokens());

        return parseText0(fileName, parserConfig, scannerConfig);
    }

    private SynNode parseText0(String fileName,
            ParserConfiguration parserConfig,
            ScannerConfiguration scannerConfig) throws SynException, IOException
    {
        SourceDescriptor sourceDescriptor = new StringSourceDescriptor("<input>");

        InputStream in = getClass().getResourceAsStream(fileName);
        try {
            Reader reader = new InputStreamReader(in);
            DefaultTokenStream tokenStream = new DefaultTokenStream(sourceDescriptor, scannerConfig, reader);
            ParserState startState = parserConfig.getStartState("Grammar");
            ParserEngine engine = new ParserEngine(tokenStream, parserConfig, startState, false);
            SynNode result = engine.parse().getRootNode();
            return result;
        } finally {
            in.close();
        }
    }
}
