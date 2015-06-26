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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Converts a SYN Abstract Syntax Tree to an EBNF grammar.
 */
final class SynTreeToEbnfGrammarConverter {
    
    private final List<EbnfNonterminal> startNonterminals = new ArrayList<>();
    private final List<EbnfTerminalElement> terminals = new ArrayList<>();
    private final Map<String, EbnfNonterminal> definedNonterminalMap = new HashMap<>();
    private final Map<String, EbnfNonterminal> undefinedNonterminalMap = new HashMap<>();
    private final Map<String, TokenDescriptor> literalToTokenDescriptorMap = new HashMap<>();
    private final Map<TokenType, TokenDescriptor> tokenTypeToTokenDescriptorMap = new HashMap<>();
    
    private SynTreeToEbnfGrammarConverter(){}
    
    /**
     * Converts an AST to an EBNF grammar.
     */
    static EbnfGrammar convert(SourceDescriptor sourceDescriptor, SynNode grammar) throws SynException {
        assert sourceDescriptor != null;
        SynTreeToEbnfGrammarConverter converter = new SynTreeToEbnfGrammarConverter();
        EbnfGrammar result = converter.convertGrammar(sourceDescriptor, grammar);
        return result;
    }

    /**
     * Converts an AST to a grammar (instance method).
     */
    private EbnfGrammar convertGrammar(SourceDescriptor sourceDescriptor, SynNode grammarNode) throws SynException {
        //Convert each nonterminal.
        ArrayNode array = (ArrayNode) grammarNode;
        for (SynNode nonterminalNode : array) {
            convertNonterminal(nonterminalNode);
        }
        
        //Fail if there are undefined nonterminals referenced from grammar rules.
        checkUndefinedNonterminals(sourceDescriptor);
        
        EbnfGrammar result = new EbnfGrammar(startNonterminals, terminals);
        return result;
    }

    private void checkUndefinedNonterminals(SourceDescriptor sourceDescriptor) throws SynGrammarException {
        if (!undefinedNonterminalMap.isEmpty()) {
            List<String> list = new ArrayList<>(undefinedNonterminalMap.keySet());
            Collections.sort(list);
            TextPos pos = new TextPos(sourceDescriptor);
            throw new SynGrammarException(pos, "Undefined nonterminals are referenced in the grammar: " + list);
        }
    }
    
    /**
     * Converts an AST node to an EBNF nonterminal definition.
     */
    private void convertNonterminal(SynNode nonterminalNode) throws SynException {
        ObjectNode object = (ObjectNode) nonterminalNode;
        String name = object.getString("name");
        TextPos namePos = object.getPos("name");
        SynNode start = object.get("start");
        SynNode rules = object.get("rules");

        TokenType tokenType = TokenTypeResolver.getTokenType(name);  
        if (tokenType != null) {
            throw new SynGrammarException(namePos, 
                    "Token name is used as a nonterminal name: " + tokenType);
        }

        if (definedNonterminalMap.containsKey(name)) {
            throw new SynGrammarException(namePos, "Nonterminal is already defined: " + name);
        }
        
        //Get or create an EBNF nonterminal.
        EbnfNonterminal nonterminal = undefinedNonterminalMap.remove(name);
        if (nonterminal == null) {
            nonterminal = new EbnfNonterminal(name);
        }

        //Put the created nonterminal into the map.
        definedNonterminalMap.put(name, nonterminal);

        //Convert productions.
        EbnfProductions productions = convertRules(rules);
        nonterminal.setProductions(productions);
        
        //Track start nonterminal.
        if (start != null) {
            startNonterminals.add(nonterminal);
        }
    }
    
    /**
     * Converts AST nodes to EBNF productions.
     */
    private EbnfProductions convertRules(SynNode rulesNode) throws SynException {
        List<EbnfProduction> productionList = new ArrayList<>();
        
        ArrayNode arrayNode = (ArrayNode) rulesNode;
        for (SynNode ruleNode : arrayNode) {
            EbnfProduction production = convertRule(ruleNode);
            productionList.add(production);
        }
        
        EbnfProductions result = new EbnfProductions(productionList);
        return result;
    }
    
    /**
     * Converts an AST node to an EBNF production.
     */
    private EbnfProduction convertRule(SynNode ruleNode) throws SynException {
        List<EbnfElement> elementList = new ArrayList<>();
        
        ArrayNode arrayNode = (ArrayNode) ruleNode;
        for (SynNode elementNode : arrayNode) {
            EbnfElement element = convertElement(elementNode);
            elementList.add(element);
        }
        
        EbnfProduction result = new EbnfProduction(elementList);
        return result;
    }
    
    /**
     * Converts an AST node to an EBNF element.
     */
    private EbnfElement convertElement(SynNode elementNode) throws SynException {
        ObjectNode objectNode = (ObjectNode) elementNode;
        TextPos keyPos = objectNode.getPos("key");
        String key = objectNode.getString("key");
        ObjectNode subElement = (ObjectNode) objectNode.get("element");
        String type = subElement.getString("type");
        
        EbnfElement result;
        if ("value".equals(type)) {
            result = convertValueElement(subElement, key, keyPos);
        } else if ("identifier".equals(type)) {
            result = convertIdentifierElement(subElement, key, keyPos);
        } else if ("lex".equals(type)) {
            result = convertLiteralElement(subElement, key, keyPos);
        } else if ("optional".equals(type)) {
            result = convertOptionalElement(subElement, key, keyPos);
        } else if ("repeat".equals(type)) {
            result = convertRepetitionElement(subElement, key, keyPos);
        } else if ("nested".equals(type)) {
            result = convertNestedElement(subElement, key, keyPos);
        } else {
            throw new IllegalStateException("Invalid SubElement type: " + type);
        }
        
        return result;
    }
    
    /**
     * Converts an AST node to an EBNF value element.
     */
    private static EbnfElement convertValueElement(ObjectNode subElement, String key, TextPos keyPos)
            throws SynGrammarException 
    {
        SynNode elementValue = subElement.get("value");
        ValueNode userNode = createValueNode(elementValue);
        EbnfElement result = new EbnfValueElement(key, keyPos, userNode);
        return result;
    }

    /**
     * Converts an AST node to a SYN value node.
     */
    private static ValueNode createValueNode(SynNode elementValue) throws SynGrammarException {
        if (elementValue == null) {
            return null;
        }

        ObjectNode objectNode = (ObjectNode) elementValue;
        SynNode value = objectNode.get("value");
        if (value == null) {
            return null;
        } else if (value instanceof ValueNode) {
            ValueNode valueNode = (ValueNode) value;
            return convertSimpleValueNode(objectNode, valueNode);
        } else {
            ObjectNode valueObjectNode = (ObjectNode) value;
            return convertNativeValue(valueObjectNode);
        }
    }
    
    /**
     * Converts an AST node to a simple SYN value node.
     */
    private static ValueNode convertSimpleValueNode(ObjectNode objectNode, ValueNode valueNode) {
        //For numeric values, a minus sign can be specified.
        boolean negative = objectNode.get("minus") != null;
        
        if (valueNode == null) {
            //Null.
            return null;
        } else if (valueNode.getValueType() == SynValueType.STRING) {
            //String.
            return new StringValueNode(null, valueNode.getString());
        } else if (valueNode.getValueType() == SynValueType.INTEGER) {
            //Integer.
            long x = valueNode.getLong();
            if (negative) {
                x = -x;
            }
            return makeBasicIntegerNode(x); 
        } else if (valueNode.getValueType() == SynValueType.FLOAT) {
            //Floating-point.
            double x = valueNode.getFloat();
            if (negative) {
                x = -x;
            }
            return new FloatValueNode(null, x);
        } else if (valueNode.getValueType() == SynValueType.BOOLEAN) {
            //Boolean.
            return BooleanValueNode.getInstance(valueNode.getBoolean());
        } else if (valueNode.getValueType() == SynValueType.OBJECT) {
            //Arbitrary object.
            return new ObjectValueNode(valueNode.getValue());
        }
        
        throw new IllegalStateException("Invalid value type: " + valueNode.getValueType());
    }
    
    /**
     * Converts an AST node to a native SYN value node.
     */
    private static ValueNode convertNativeValue(ObjectNode nativeValueNode) throws SynGrammarException {
        ArrayNode classNameNode = (ArrayNode) nativeValueNode.get("className");
        String className = arrayToClassName(classNameNode);
        String fieldName = nativeValueNode.getString("fieldName");
        
        Object value = getNativeFieldValue(nativeValueNode, classNameNode, className, fieldName);
        ValueNode result = nativeValueToValueNode(nativeValueNode, fieldName, value);
        return result;
    }

    /**
     * Returns the value of the specified Java static field.
     */
    private static Object getNativeFieldValue(
            ObjectNode nativeValueNode,
            ArrayNode classNameNode,
            String className,
            String fieldName) throws SynGrammarException
    {
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getField(fieldName);
            
            int modifiers = field.getModifiers();
            if (!Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) {
                TextPos fieldNamePos = nativeValueNode.getPos("fieldName");
                throw new SynGrammarException(fieldNamePos,
                        String.format("Field %s is not a static final field", fieldName));
            }
            
            Object value = field.get(clazz);
            return value;
        } catch (IllegalAccessException | SecurityException | NoSuchFieldException e) {
            TextPos fieldNamePos = nativeValueNode.getPos("fieldName");
            throw new SynGrammarException(fieldNamePos,
                    String.format("Failed to get the value of field %s.%s", className, fieldName), e);
        } catch (ClassNotFoundException e) {
            TextPos classNamePos = getClassNamePos(classNameNode);
            throw new SynGrammarException(classNamePos, "Class not found: " + className);
        }
    }

    /**
     * Converts an arbitrary Java object to a SYN value node.
     */
    private static ValueNode nativeValueToValueNode(ObjectNode nativeValueNode, String fieldName, Object value)
            throws SynGrammarException
    {
        if (value instanceof Byte
                || value instanceof Short
                || value instanceof Character
                || value instanceof Integer
                || value instanceof Long) 
        {
            //Integer.
            long longValue = ((Number) value).longValue();
            return makeBasicIntegerNode(longValue);
        } else if (value instanceof Float || value instanceof Double) {
            //Floating-point.
            double doubleValue = ((Number) value).doubleValue();
            return new FloatValueNode(null, doubleValue);
        } else if (value instanceof String) {
            //String.
            return new StringValueNode(null, (String) value);
        } else if (value instanceof Boolean) {
            //Boolean.
            return BooleanValueNode.getInstance(((Boolean) value).booleanValue());
        } else if (value != null) {
            //Arbitrary object.
            return new ObjectValueNode(value);
        }
        
        //Null value is not supported.
        TextPos fieldNamePos = nativeValueNode.getPos("fieldName");
        throw new SynGrammarException(fieldNamePos,
                String.format("The value of field %s is null", fieldName));
    }

    /**
     * Gets an EBNF element by name. Returns either a terminal, or a nonterminal element.
     */
    private EbnfElement convertIdentifierElement(ObjectNode subElement, String key, TextPos keyPos) {
        String name = subElement.getString("value");

        TokenType tokenType = TokenTypeResolver.getTokenType(name);
        if (tokenType != null) {
            //Literal terminal symbol.
            TokenDescriptor tokenDescriptor = tokenTypeToTokenDescriptorMap.get(tokenType);
            if (tokenDescriptor == null) {
                tokenDescriptor = TokenDescriptor.forType(tokenType);
                tokenTypeToTokenDescriptorMap.put(tokenType, tokenDescriptor);
            }
            EbnfTerminalElement element = new EbnfTerminalElement(key, keyPos, tokenDescriptor);
            terminals.add(element);
            return element;
        }
        
        //Must be a nonterminal otherwise.
        EbnfNonterminal nonterminal = definedNonterminalMap.get(name);
        if (nonterminal == null) {
            nonterminal = undefinedNonterminalMap.get(name);
            if (nonterminal == null) {
                nonterminal = new EbnfNonterminal(name);
                undefinedNonterminalMap.put(name, nonterminal);
            }
        }
        return new EbnfNonterminalElement(key, keyPos, nonterminal);
    }
    
    /**
     * Converts a string literal to an EBNF terminal element. 
     */
    private EbnfElement convertLiteralElement(ObjectNode subElement, String key, TextPos keyPos) 
            throws SynException 
    {
        TextPos literalPos = subElement.getPos("value");
        String literal = subElement.getString("value");
        
        TokenDescriptor tokenDescriptor = literalToTokenDescriptorMap.get(literal);
        if (tokenDescriptor == null) {
            //This literal has not been seen before.
            if (literal.length() == 0) {
                throw new SynGrammarException(literalPos, "Literal cannot be an empty string");
            }
            if (!TokenDescriptor.isKeycharLiteral(literal) && !TokenDescriptor.isKeywordLiteral(literal)) {
                throw new SynGrammarException(literalPos, String.format("Invalid literal: '%s'", literal));
            }
            tokenDescriptor = TokenDescriptor.forLiteral(literal);
            literalToTokenDescriptorMap.put(literal, tokenDescriptor);
        }
        
        EbnfTerminalElement element = new EbnfTerminalElement(key, keyPos, tokenDescriptor);
        terminals.add(element);
        return element;
    }
    
    /**
     * Converts an AST node to an optional EBNF element.
     */
    private EbnfElement convertOptionalElement(ObjectNode subElement, String key, TextPos keyPos)
            throws SynException 
    {
        SynNode rules = subElement.get("rules");
        EbnfProductions productions = convertRules(rules);
        EbnfElement result = new EbnfOptionalElement(key, keyPos, productions);
        return result;
    }
    
    /**
     * Converts an AST node to a repetition EBNF element.
     */
    private EbnfElement convertRepetitionElement(ObjectNode subElement, String key, TextPos keyPos) 
            throws SynException 
    {
        boolean nullable = subElement.getBoolean("nullable");
        SynNode body = subElement.get("body");
        EbnfProductions bodyProductions = convertRules(body);
        SynNode separator = subElement.get("separator");
        
        EbnfProductions separatorProductions = null;
        if (separator != null) {
            separatorProductions = convertRules(separator);
        }

        EbnfElement result =
                new EbnfRepetitionElement(key, keyPos, bodyProductions, separatorProductions, nullable);
        return result;
    }
    
    /**
     * Converts an AST node to a nested EBNF element.
     */
    private EbnfElement convertNestedElement(ObjectNode subElement, String key, TextPos keyPos)
            throws SynException
    {
        SynNode rules = subElement.get("rules");
        EbnfProductions productions = convertRules(rules);
        EbnfElement result = new EbnfNestedElement(key, keyPos, productions);
        return result;
    }
    
    /**
     * Converts an AST array node to a Java canonical class name.
     */
    private static String arrayToClassName(ArrayNode arrayNode) {
        StringBuilder bld = new StringBuilder();
        String sep = "";
        for (SynNode node : arrayNode) {
            String element = ((ValueNode) node).getString();
            bld.append(sep);
            bld.append(element);
            sep = ".";
        }
        String result = bld.toString();
        return result;
    }
    
    /**
     * Returns the text position associated with the given AST node array.
     */
    private static TextPos getClassNamePos(ArrayNode arrayNode) {
        TextPos result;
        if (arrayNode.size() > 1) {
            TextPos startPos = arrayNode.getPos(0);
            TextPos endPos = arrayNode.getPos(arrayNode.size() - 1);
            result = new TextPos(startPos.getSource(), startPos.getOffset(), startPos.getLine(),
                    startPos.getColumn(), endPos.getOffset() + endPos.getLength() - startPos.getOffset());
        } else {
            result = arrayNode.getPos(0);
        }
        return result;
    }
    
    /**
     * Creates a SYN value node for the specified <code>long</code> value.
     */
    private static ValueNode makeBasicIntegerNode(long x) {
        ValueNode userNode;
        if (x >= Integer.MIN_VALUE && x <= Integer.MAX_VALUE) {
            userNode = new IntegerValueNode(null, (int) x);
        } else {
            userNode = new LongValueNode(null, x);
        }
        return userNode;
    }
}
