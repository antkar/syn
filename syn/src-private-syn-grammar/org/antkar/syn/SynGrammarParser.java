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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.antkar.syn.BnfGrammar;
import org.antkar.syn.DefaultTokenStream;
import org.antkar.syn.EbnfGrammar;
import org.antkar.syn.ParserConfiguration;
import org.antkar.syn.ParserConfigurator;
import org.antkar.syn.ParserEngine;
import org.antkar.syn.ParserState;
import org.antkar.syn.ScannerConfiguration;
import org.antkar.syn.ScannerConfigurator;
import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.SynNode;
import org.antkar.syn.SynResult;
import org.antkar.syn.TokenDescriptor;
import org.xml.sax.SAXException;

/**
 * Parser for a SYN grammar.
 */
class SynGrammarParser {
    private static final String GRAMMAR_FILE_NAME = "syn_grammar.xml";
    private static final String GRAMMAR_START_NONTERMINAL = "Grammar";
    
    private static ParserConfiguration SynGrammarParserConfiguration = null;
    
    private SynGrammarParser() {
    }
    
    /**
     * Parses the specified SYN grammar and constructs an EBNF grammar object.
     */
    static EbnfGrammar parseGrammar(Reader grammarReader, SourceDescriptor grammarDescriptor)
            throws SynException
    {
        assert grammarDescriptor != null;
        
        //Get parser configuration for the grammar of grammar.
        ParserConfiguration parserConfig = getSynGrammarParserConfiguration();
        ParserState startState = parserConfig.getStartState(GRAMMAR_START_NONTERMINAL);
        if (startState == null) {
            throw new IllegalStateException("Syn grammar start nonterminal not found: "
                    + GRAMMAR_START_NONTERMINAL);
        }
        
        //Create a token stream.
        List<TokenDescriptor> tokenDescriptors = parserConfig.getTokenDescriptors();
        ScannerConfiguration scannerConfig = ScannerConfigurator.makeConfiguration(tokenDescriptors);
        DefaultTokenStream tokenStream = new DefaultTokenStream(grammarDescriptor, scannerConfig, grammarReader);

        //Create a parser engine.
        ParserEngine engine = new ParserEngine(tokenStream, parserConfig, startState, false);
        
        //Parse the grammar input.
        SynResult sGrammar = engine.parse();
        SynNode grammarNode = sGrammar.getRootNode();

        //Convert the obtained AST to an EBNF grammar.
        EbnfGrammar result = SynTreeToEbnfGrammarConverter.convert(grammarDescriptor, grammarNode);
        return result;
    }
    
    /**
     * Returns an existing or creates a new SYN grammar parser configuration.
     */
    private static ParserConfiguration getSynGrammarParserConfiguration() {
        ParserConfiguration result = SynGrammarParserConfiguration;
        if (result == null) {
            result = getSynGrammarParserConfigurationThreadSafe();
        }
        return result;
    }
    
    private synchronized static ParserConfiguration getSynGrammarParserConfigurationThreadSafe() {
        if (SynGrammarParserConfiguration == null) {
            try {
                URL url = SynGrammarParser.class.getResource(GRAMMAR_FILE_NAME);
                if (url == null) {
                    throw new FileNotFoundException(GRAMMAR_FILE_NAME);
                }
                
                //Load EBNF grammar from XML.
                EbnfGrammar eGrammar = XmlGrammarParser.parseGrammar(url);
                
                //Convert EBNF grammar to BNF grammar.
                BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);
                
                //Create a parser configuration from the BNF grammar.
                SynGrammarParserConfiguration = ParserConfigurator.makeConfiguration(bGrammar);
            } catch (IOException | SAXException | ParserConfigurationException | SynException e) {
                throw new IllegalStateException(e);
            }
        }
        return SynGrammarParserConfiguration;
    }
}
