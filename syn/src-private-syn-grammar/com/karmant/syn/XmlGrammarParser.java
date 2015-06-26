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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * A parser for the Grammar of SYN Grammar. The Grammar of Grammar is written in XML.
 */
final class XmlGrammarParser {
    
    private final List<EbnfNonterminal> startNonterminals = new ArrayList<>();
    private final List<EbnfTerminalElement> terminals = new ArrayList<>();
    private final Map<String, EbnfNonterminal> nameToNonterminalMap = new HashMap<>();
    private final Map<String, TokenDescriptor> nameToTerminalMap = new HashMap<>();
    private final Map<String, TokenDescriptor> literalToTerminalMap = new HashMap<>();
    
    private XmlGrammarParser() {}
    
    /**
     * Parses the specified resource and returns an EBNF grammar. The resource must be an XML file.
     */
    static EbnfGrammar parseGrammar(URL url) throws SAXException, IOException, ParserConfigurationException {
        XmlGrammarParser grammarParser = new XmlGrammarParser();
        EbnfGrammar result = grammarParser.parseGrammarPrivate(url);
        return result;
    }
    
    private EbnfGrammar parseGrammarPrivate(URL url) throws SAXException, IOException, ParserConfigurationException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XmlHandler handler = new XmlHandler();
        
        parser.parse(url.toExternalForm(), handler);
        
        XmlNode node = handler.getRootNode();
        EbnfGrammar result = xmlTreeToGrammar(node);
        return result;
    }
    
    /**
     * Converts an XML node to an EBNF grammar.
     */
    private EbnfGrammar xmlTreeToGrammar(XmlNode node) {
        assert "grammar".equals(node.getName());
        for (XmlNode subNode : node.getNestedNodes()) {
            convertNonterminal(subNode);
        }
        assert !startNonterminals.isEmpty();
        
        EbnfGrammar ebnfGrammar = new EbnfGrammar(startNonterminals, terminals);
        for (EbnfNonterminal eNonterminal : nameToNonterminalMap.values()) {
            eNonterminal.getProductions();
        }
        
        return ebnfGrammar;
    }
    
    /**
     * Creates an EBNF nonterminal defined by the given XML node.
     */
    private void convertNonterminal(XmlNode node) {
        assert "nonterminal".equals(node.getName());
        String name = node.getAttributes().get("name");
        String start = node.getAttributes().get("start");
        assert name != null;
        
        //Check if it is the start nonterminal.
        boolean isStart = false;
        if (start != null) {
            assert "true".equals(start);
            isStart = true;
        }
        
        //Get or create an EBNF nonterminal.
        EbnfNonterminal nonterminal = getNonterminalByName(name);
        if (isStart) {
            startNonterminals.add(nonterminal);
        }
        
        //Convert productions.
        List<EbnfProduction> productionList = new ArrayList<>();
        for (XmlNode subNode : node.getNestedNodes()) {
            EbnfProduction production = convertRule(subNode);
            productionList.add(production);
        }
        
        EbnfProductions productions = new EbnfProductions(productionList);
        nonterminal.setProductions(productions);
    }
    
    /**
     * Converts an XML node to an EBNF production.
     */
    private EbnfProduction convertRule(XmlNode node) {
        List<EbnfElement> elements = new ArrayList<>();
        
        for (XmlNode subNode : node.getNestedNodes()) {
            EbnfElement element = convertElement(subNode);
            elements.add(element);
        }
        
        EbnfProduction production = new EbnfProduction(elements);
        return production;
    }
    
    /**
     * Converts an XML node to an EBNF element.
     */
    private EbnfElement convertElement(XmlNode node) {
        String name = node.getName();
        
        EbnfElement element;
        if ("tk".equals(name)) {
            element = convertTk(node);
        } else if ("nt".equals(name)) {
            element = convertNt(node);
        } else if ("vl".equals(name)) {
            element = convertVl(node);
        } else if ("opt".equals(name)) {
            element = convertOpt(node);
        } else if ("rep".equals(name)) {
            element = convertRep(node);
        } else {
            throw new IllegalStateException(name);
        }
        
        return element;
    }
    
    /**
     * Converts an XML node to an EBNF terminal element.
     */
    private EbnfElement convertTk(XmlNode node) {
        assert "tk".equals(node.getName());
        String key = node.getAttributes().get("key");
        String name = node.getAttributes().get("name");
        String literal = node.getAttributes().get("lex");
        assert name == null || literal == null;
        
        TokenDescriptor tokenDescriptor;
        if (name != null) {
            tokenDescriptor = getTerminalByName(name);
        } else if (literal != null) {
            tokenDescriptor = getTerminalByLiteral(literal);
        } else {
            throw new IllegalStateException();
        }
        
        EbnfTerminalElement element = new EbnfTerminalElement(key, null, tokenDescriptor);
        terminals.add(element);
        return element;
    }
    
    /**
     * Converts an XML node to an EBNF nonterminal element. 
     */
    private EbnfElement convertNt(XmlNode node) {
        assert "nt".equals(node.getName());
        String name = node.getAttributes().get("name");
        String key = node.getAttributes().get("key");
        EbnfNonterminal nonterminal = getNonterminalByName(name);
        
        EbnfElement element = new EbnfNonterminalElement(key, null, nonterminal);
        return element;
    }

    /**
     * Converts an XML node to an EBNF value element. 
     */
    private EbnfElement convertVl(XmlNode node) {
        assert "vl".equals(node.getName());
        
        String key = node.getAttributes().get("key");
        String valueStr = node.getAttributes().get("value");
        assert valueStr != null;
        ValueNode valueNode = getValueNode(valueStr);
        
        EbnfElement element = new EbnfValueElement(key, null, valueNode);
        return element;
    }

    /**
     * Converts an XML node to an EBNF optional element. 
     */
    private EbnfElement convertOpt(XmlNode node) {
        assert "opt".equals(node.getName());
        
        String key = node.getAttributes().get("key");
        EbnfProduction production = convertRule(node);
        EbnfProductions productions = makeProductions(production);
        
        EbnfOptionalElement element = new EbnfOptionalElement(key, null, productions);
        return element;
    }

    /**
     * Converts an XML node to an EBNF repetition element. 
     */
    private EbnfElement convertRep(XmlNode node) {
        assert "rep".equals(node.getName());
        
        //Find out properties.
        String key = node.getAttributes().get("key");
        String nullable = node.getAttributes().get("nullable");
        assert "true".equals(nullable) || "false".equals(nullable);
        boolean isNullable = "true".equals(nullable);
        
        //Convert sub-elements. 
        List<EbnfElement> elements = new ArrayList<>();
        XmlNode separator = null;
        for (XmlNode subNode : node.getNestedNodes()) {
            if ("separator".equals(subNode.getName())) {
                separator = subNode;
                break;
            }
            
            EbnfElement element = convertElement(subNode);
            elements.add(element);
        }
        
        EbnfProduction bodyProduction = new EbnfProduction(elements);
        EbnfProductions bodyProductions = makeProductions(bodyProduction);
        EbnfProductions separatorProductions = null;
        
        //Convert separator.
        if (separator != null) {
            EbnfProduction separatorProduction = convertRule(separator);
            separatorProductions = makeProductions(separatorProduction);
        }
        
        //Create an EBNF element.
        EbnfRepetitionElement element = new EbnfRepetitionElement(key, null, bodyProductions, 
                separatorProductions, isNullable);
        
        return element;
    }
    
    /**
     * Returns an existing or a newly created EBNF nonterminal with the specified name.
     */
    private EbnfNonterminal getNonterminalByName(String name) {
        EbnfNonterminal nonterminal = nameToNonterminalMap.get(name);
        if (nonterminal == null) {
            nonterminal = new EbnfNonterminal(name);
            nameToNonterminalMap.put(name, nonterminal);
        }
        return nonterminal;
    }

    /**
     * Returns an existing or a newly created token with the given name.
     */
    private TokenDescriptor getTerminalByName(String name) {
        TokenDescriptor terminal = nameToTerminalMap.get(name);
        
        if (terminal == null) {
            TokenType tokenType = TokenTypeResolver.getTokenType(name);
            if (tokenType == null) {
                throw new IllegalStateException(name);
            }
            terminal = TokenDescriptor.forType(tokenType);
            nameToTerminalMap.put(name, terminal);
        }
        
        return terminal;
    }

    /**
     * Returns an existing or a newly created custom token with the given literal.
     */
    private TokenDescriptor getTerminalByLiteral(String literal) {
        assert literal.length() > 0;
        
        TokenDescriptor terminal = literalToTerminalMap.get(literal);
        if (terminal == null) {
            terminal = TokenDescriptor.forLiteral(literal);
            literalToTerminalMap.put(literal, terminal);
        }
        
        return terminal;
    }
    
    /**
     * Creates a SYN value node from a string.
     */
    private static ValueNode getValueNode(String s) {
        ValueNode value;
        
        if ("null".equals(s)) {
            value = null;
        } else if ("true".equals(s)) {
            value = BooleanValueNode.TRUE;
        } else if ("false".equals(s)) {
            value = BooleanValueNode.FALSE;
        } else {
            value = getIntegerValue(s);
            if (value == null) {
                value = getFloatingPointValue(s);
                if (value == null) {
                    value = new StringValueNode(null, s);
                }
            }
        }
        
        return value;
    }

    /**
     * Tries to parse a string as an integer value.
     */
    private static ValueNode getIntegerValue(String s) {
        ValueNode value = null;
        
        try {
            long longValue = Long.valueOf(s);
            if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                value = new IntegerValueNode(null, (int) longValue);
            } else {
                value = new LongValueNode(null, longValue);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        
        return value;
    }

    /**
     * Tries to parse a string as a floating-point value.
     */
    private static ValueNode getFloatingPointValue(String s) {
        try {
            double doubleValue = Double.valueOf(s);
            return new FloatValueNode(null, doubleValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private static EbnfProductions makeProductions(EbnfProduction production) {
        List<EbnfProduction> productionList = new ArrayList<>();
        productionList.add(production);
        EbnfProductions productions = new EbnfProductions(productionList);
        return productions;
    }
}
