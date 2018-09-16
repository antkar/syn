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
import java.io.Reader;

import org.antkar.syn.internal.CommonUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link SynParser}.
 */
public class SynParserSimpleTest extends Assert {

    @Test
    public void testConstructSuccess() throws Exception {
        createParser("SynParserSimpleTest_ConstructSuccess_grammar.txt");
    }

    @Test
    public void testConstructFailSyntaxError() throws Exception {
        try {
            createParserStr("@expr : expr '+' t | t ; @ syntax_error aaaabbbb ; t : ID;");
            fail();
        } catch (SynSyntaxException e) {
            assertEquals(TokenDescriptor.forType(TokenType.ID), e.getActualToken().getTokenDescriptor());
            assertEquals(1, e.getExpectedTokens().size());
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral(":")));
        }
    }

    @Test
    public void testConstructFailUndefinedNonterminal() {
        try {
            createParserStr("@expr : expr '+' t | t ; t : ID | Undefined_Nonterminal ;");
            fail();
        } catch (SynException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(
                    "Undefined nonterminals are referenced in the grammar: [Undefined_Nonterminal]"));
        }
    }

    @Test
    public void testConstructFailDuplicatedNonterminal() {
        try {
            createParserStr("@expr : expr '+' t | t ; t : ID ; expr : 'duplicated_nonterminal' ;");
            fail();
        } catch (SynException e) {
            assertTrue(e.getMessage(), e.getMessage().contains("Nonterminal is already defined: expr"));
        }
    }

    @Test
    public void testConstructFailNonterminalTerminalName() {
        try {
            createParserStr("@expr : expr '+' t | t ; t : ID ; INTEGER : 'bad_nonterminal_name' ;");
            fail();
        } catch (SynException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(
                    "Token name is used as a nonterminal name: INTEGER"));
        }
    }

    @Test
    public void testParseSuccess() throws Exception {
        SynParser synParser = createParser("SynParserSimpleTest_ParseSuccess_grammar.txt");
        parseStr(
                synParser,
                "a = 1 + 2 - 3 ; b = func(a / 2, 'hello', x); c = func2(func3(func4((a + b) * c / 3.14), x), y);",
                "file");
    }

    @Test
    public void testParseSyntaxError() throws Exception {
        SynParser synParser = createParser("SynParserSimpleTest_ParseSuccess_grammar.txt");
        try {
            parse(synParser, "SynParserSimpleTest_ParseSyntaxError_text.txt", "file");
            fail();
        } catch (SynSyntaxException e) {
            assertEquals(TokenDescriptor.forLiteral("("), e.getActualToken().getTokenDescriptor());
            assertEquals(1, e.getExpectedTokens().size());
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forType(TokenType.ID)));
        }
    }

    @Test
    public void testParseUnexpectedEof() throws Exception {
        SynParser synParser = createParser("SynParserSimpleTest_ParseSuccess_grammar.txt");
        try {
            parseStr(synParser, "a = 1 + 2 - 3 ; b = func(a / 2, 'hello', x)", "file");
            fail();
        } catch (SynSyntaxException e) {
            assertEquals(TokenDescriptor.forType(TokenType.END_OF_FILE), e.getActualToken().getTokenDescriptor());
            assertEquals(5, e.getExpectedTokens().size());
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("/")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("+")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("-")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral("*")));
            assertTrue(e.getExpectedTokens().contains(TokenDescriptor.forLiteral(";")));
        }
    }

    @Test
    public void testParseTree() throws Exception {
        SynParser synParser = createParser("SynParserSimpleTest_ParseTree_grammar.txt");
        SynNode tree = parseStr(synParser, "{ name = 'Foo' ; age = 30 ; } [ ] [ 'hello' ] [ 1, 2, 3 ]", "file");
        ArrayNode array = (ArrayNode) tree;
        assertEquals(4, array.size());

        ArrayNode value1 = (ArrayNode) array.get(0);
        ObjectNode field1 = (ObjectNode) value1.get(0);
        assertEquals("name", field1.getString("name"));
        assertEquals("Foo", field1.getString("value"));
        ObjectNode field2 = (ObjectNode) value1.get(1);
        assertEquals("age", field2.getString("name"));
        assertEquals(30, field2.getInt("value"));

        ArrayNode value2 = (ArrayNode) array.get(1);
        assertEquals(0, value2.size());

        ArrayNode value3 = (ArrayNode) array.get(2);
        assertEquals(1, value3.size());
        assertEquals("hello", value3.getString(0));

        ArrayNode value4 = (ArrayNode) array.get(3);
        assertEquals(3, value4.size());
        assertEquals(1, value4.getInt(0));
        assertEquals(2, value4.getInt(1));
        assertEquals(3, value4.getInt(2));
    }

    @Test
    public void testParseAmbiguityFail() throws Exception {
        SynParser synParser = createParserStr(
                "@file : ( stmt ) * ;" +
                "stmt : if | call ;" +
                "call : ID '(' ')' ';' ;" +
                "if : 'if' '(' ID ')' stmt ( 'else' stmt )? ;");
        synParser.setFailOnAmbiguity(true);
        try {
            parseStr(synParser, "if (a) if (b) func1(); else func2();", "file");
            fail();
        } catch (SynAmbiguityException e) {
            // ok
        }
    }

    @Test
    public void testParseResolveDanglingElseAmbiguity1() throws Exception {
        String grammar =
                "@file : ( stmt ) * ;" +
                "stmt : if | call ;" +
                "call : name=ID ;" +
                "if : 'if' cond=ID true_stmt=stmt false_stmt=( 'else' stmt )? ;";

        checkResolveDanglingElseAmbiguity(grammar);
    }

    @Test
    public void testParseResolveDanglingElseAmbiguity2() throws Exception {
        String grammar =
                "@file : ( stmt ) * ;" +
                "stmt : if | call ;" +
                "call : name=ID ;" +
                "if : 'if' cond=ID true_stmt=stmt | " +
                "'if' cond=ID true_stmt=stmt 'else' false_stmt=stmt ;";

        checkResolveDanglingElseAmbiguity(grammar);
    }

    @Test
    public void testParseResolveDanglingElseAmbiguity3() throws Exception {
        String grammar =
                "@file : ( stmt ) * ;" +
                "stmt : if | call ;" +
                "call : name=ID ;" +
                "if : 'if' cond=ID true_stmt=stmt 'else' false_stmt=stmt | " +
                "'if' cond=ID true_stmt=stmt ;";

        checkResolveDanglingElseAmbiguity(grammar);
    }

    private void checkResolveDanglingElseAmbiguity(String grammar) throws SynException {
        SynParser synParser = createParserStr(grammar);
        SynNode tree = parseStr(synParser, "if a if b func1 else func2", "file");
        ArrayNode file = (ArrayNode) tree;

        ObjectNode if1 = (ObjectNode) file.get(0);
        ObjectNode if1true = (ObjectNode) if1.get("true_stmt");
        ObjectNode if1false = (ObjectNode) if1.get("false_stmt");
        assertNull(if1false);
        ObjectNode f2false = (ObjectNode) if1true.get("false_stmt");
        assertNotNull(f2false);
        assertEquals("func2", f2false.getString("name"));
    }

    @Test
    public void testParseResolveExpressionAmbiguity() throws Exception {
        SynParser synParser = createParserStr("@expr : left=expr '+' right=expr | result=ID ;");
        SynNode tree = parseStr(synParser, "a + b + c", "expr");

        ObjectNode exprABC = (ObjectNode)tree;

        ObjectNode exprAB = (ObjectNode)exprABC.get("left");
        assertNotNull(exprAB);

        ValueNode exprA = (ValueNode)exprAB.get("left");
        assertNotNull(exprA);
        assertEquals("a", exprA.getString());

        ValueNode exprB = (ValueNode)exprAB.get("right");
        assertNotNull(exprB);
        assertEquals("b", exprB.getString());

        ValueNode exprC = (ValueNode)exprABC.get("right");
        assertNotNull(exprC);
        assertEquals("c", exprC.getString());
    }

    @Test
    public void testParseReduceCycle() throws Exception {
        SynParser synParser = createParserStr("@file : A ; A : B | 'a' ; B : C | 'b' ; C : A | 'c' ;");

        // Parser should not fail because reduce cycle should not be considered to be ambiguity.
        synParser.setFailOnAmbiguity(true);
        parseStr(synParser, "a", "file");
    }

    @Test
    public void testParseGrowingReduceCycle() throws Exception {
        SynParser synParser = createParserStr("@file : N ; N : L N | 'n' ; L : 'l' | ;");
        synParser.setFailOnAmbiguity(false);
        parseStr(synParser, "l n", "file");
    }

    public enum Colors {
        RED,
        GREEN,
        BLUE
    }

    public static final boolean TRUE = true;
    public static final boolean FALSE = false;
    public static final int INT = Integer.MAX_VALUE;
    public static final long LONG = Long.MAX_VALUE;
    public static final double DOUBLE = 12.34;
    public static final String STRING = "Hello";
    public static final Object OBJECT = new Object();

    @Test
    public void testObjectValue() throws Exception {
        SynParser synParser = createParserStr(
                "@file : (value)* ;" +
                "value :" +
                "'red' <org.antkar.syn.SynParserSimpleTest$Colors.RED> |" +
                "'green' <org.antkar.syn.SynParserSimpleTest$Colors.GREEN> |" +
                "'blue' <org.antkar.syn.SynParserSimpleTest$Colors.BLUE> |" +
                "'true' <org.antkar.syn.SynParserSimpleTest.TRUE> |" +
                "'false' <org.antkar.syn.SynParserSimpleTest.FALSE> |" +
                "'int' <org.antkar.syn.SynParserSimpleTest.INT> |" +
                "'long' <org.antkar.syn.SynParserSimpleTest.LONG> |" +
                "'double' <org.antkar.syn.SynParserSimpleTest.DOUBLE> |" +
                "'string' <org.antkar.syn.SynParserSimpleTest.STRING> |" +
                "'object' <org.antkar.syn.SynParserSimpleTest.OBJECT> ;");

        SynNode tree = parseStr(synParser, "red green blue true false int long double string object", "file");
        ArrayNode arrayNode = (ArrayNode) tree;

        checkObjectValue(arrayNode, 0, Colors.RED);
        checkObjectValue(arrayNode, 1, Colors.GREEN);
        checkObjectValue(arrayNode, 2, Colors.BLUE);
        checkSimpleValue(arrayNode, 3, SynValueType.BOOLEAN, TRUE);
        checkSimpleValue(arrayNode, 4, SynValueType.BOOLEAN, FALSE);
        checkSimpleValue(arrayNode, 5, SynValueType.INTEGER, INT);
        checkSimpleValue(arrayNode, 6, SynValueType.INTEGER, LONG);
        checkSimpleValue(arrayNode, 7, SynValueType.FLOAT, DOUBLE);
        checkSimpleValue(arrayNode, 8, SynValueType.STRING, STRING);
        checkObjectValue(arrayNode, 9, OBJECT);

    }

    private static void checkObjectValue(ArrayNode arrayNode, int index, Object value) {
        ValueNode valueNode = (ValueNode) arrayNode.get(index);
        assertEquals(SynValueType.OBJECT, valueNode.getValueType());
        Object actualValue = valueNode.getValue();
        assertTrue(String.valueOf(actualValue), actualValue == value);
    }

    private static void checkSimpleValue(ArrayNode arrayNode, int index, SynValueType valueType, Object value) {
        ValueNode valueNode = (ValueNode) arrayNode.get(index);
        assertEquals(valueType, valueNode.getValueType());
        Object actualValue = valueNode.getValue();
        assertEquals(value, actualValue);
    }

    @Test
    public void testValuesInNestedElement() throws Exception {
        SynParser synParser = createParser("SynParserSimpleTest_ValuesInNestedElement_grammar.txt");
        SynNode tree = parseStr(synParser,
                "field order : string?;" +
                "field values : int+;" +
                "field comments : text*;" +
                "assert aaa == bbb;" +
                "assert ccc != ddd;" +
                "assert fff < ggg;", "Unit");
        ArrayNode unitNode = (ArrayNode) tree;
        assertEquals(6, unitNode.size());

        ObjectNode field0 = (ObjectNode) unitNode.get(0);
        assertEquals("order", field0.getString("name"));
        assertEquals("string", field0.getString("type"));
        assertEquals(0, field0.getInt("cardinality"));
        assertEquals(true, field0.getBoolean("optional"));
        assertEquals(null, field0.get("many"));

        ObjectNode field1 = (ObjectNode) unitNode.get(1);
        assertEquals("values", field1.getString("name"));
        assertEquals("int", field1.getString("type"));
        assertEquals(1, field1.getInt("cardinality"));
        assertEquals(null, field1.get("optional"));
        assertEquals(true, field1.getBoolean("many"));

        ObjectNode field2 = (ObjectNode) unitNode.get(2);
        assertEquals("comments", field2.getString("name"));
        assertEquals("text", field2.getString("type"));
        assertEquals(2, field2.getInt("cardinality"));
        assertEquals(null, field2.get("optional"));
        assertEquals(true, field2.getBoolean("many"));

        ObjectNode field3 = (ObjectNode) unitNode.get(3);
        assertEquals("aaa", field3.getString("name1"));
        assertEquals("bbb", field3.getString("name2"));
        assertEquals("==", field3.getString("op"));

        ObjectNode field4 = (ObjectNode) unitNode.get(4);
        assertEquals("ccc", field4.getString("name1"));
        assertEquals("ddd", field4.getString("name2"));
        assertEquals("!=", field4.getString("op"));

        ObjectNode field5 = (ObjectNode) unitNode.get(5);
        assertEquals("fff", field5.getString("name1"));
        assertEquals("ggg", field5.getString("name2"));
        assertEquals("<", field5.getString("op"));
    }

    @Test
    public void testValuesInOptionalElement() throws Exception {
        SynParser synParser = createParser("SynParserSimpleTest_ValuesInOptionalElement_grammar.txt");
        SynNode tree = parseStr(synParser,
                "field order : string?;" +
                "field values : int+;" +
                "field comments : text*;" +
                "field exist : boolean;" +
                "assert aaa;" +
                "assert !bbb;" +
                "assert -ccc;", "Unit");

        ArrayNode unitNode = (ArrayNode) tree;
        assertEquals(7, unitNode.size());

        ObjectNode field0 = (ObjectNode) unitNode.get(0);
        assertEquals("order", field0.getString("name"));
        assertEquals("string", field0.getString("type"));
        assertEquals(0, field0.getInt("cardinality"));
        assertEquals(true, field0.getBoolean("optional"));
        assertEquals(null, field0.get("many"));

        ObjectNode field1 = (ObjectNode) unitNode.get(1);
        assertEquals("values", field1.getString("name"));
        assertEquals("int", field1.getString("type"));
        assertEquals(1, field1.getInt("cardinality"));
        assertEquals(null, field1.get("optional"));
        assertEquals(true, field1.getBoolean("many"));

        ObjectNode field2 = (ObjectNode) unitNode.get(2);
        assertEquals("comments", field2.getString("name"));
        assertEquals("text", field2.getString("type"));
        assertEquals(2, field2.getInt("cardinality"));
        assertEquals(null, field2.get("optional"));
        assertEquals(true, field2.getBoolean("many"));

        ObjectNode field3 = (ObjectNode) unitNode.get(3);
        assertEquals("exist", field3.getString("name"));
        assertEquals("boolean", field3.getString("type"));
        assertEquals(null, field3.get("cardinality"));
        assertEquals(null, field3.get("optional"));
        assertEquals(null, field3.get("many"));

        ObjectNode field4 = (ObjectNode) unitNode.get(4);
        assertEquals(null, field4.get("op"));
        assertEquals("aaa", field4.getString("name"));

        ObjectNode field5 = (ObjectNode) unitNode.get(5);
        assertEquals("!", field5.getString("op"));
        assertEquals("bbb", field5.getString("name"));

        ObjectNode field6 = (ObjectNode) unitNode.get(6);
        assertEquals("-", field6.getString("op"));
        assertEquals("ccc", field6.getString("name"));
    }

    @Test
    public void testComplexEmbeddedElements() throws Exception {

        final Object[][] testData = {
                { "x a Hello c e y" }, { "str", "Hello", "param", 5},
                { "x a FFF c f y" }, { "str", "FFF" },
                { "x a XYZ d g y" }, { "str", "XYZ", "value", true },
                { "x a case04 d h y" }, { "str", "case04", "value", false },
                { "x a case05 d y" }, { "str", "case05" },
                { "x a case06 y" }, { "str", "case06" },
                { "x a 1007 c e y" }, { "val", 1007, "param", 5 },
                { "x a 1008 c f y" }, { "val", 1008 },
                { "x a 1009 d g y" }, { "val", 1009, "value", true },
                { "x a 1010 d h y" }, { "val", 1010, "value", false },
                { "x a 1011 d y" }, { "val", 1011 },
                { "x a 1012 y" }, { "val", 1012 },
                { "x b case13 c e y" }, { "name", "case13", "param", 5},
                { "x b case14 c f y" }, { "name", "case14" },
                { "x b case15 d g y" }, { "name", "case15", "value", true },
                { "x b case16 d h y" }, { "name", "case16", "value", false },
                { "x b case17 d y" }, { "name", "case17" },
                { "x b case18 y" }, { "name", "case18" },
                { "x b \"case19\" c e y" }, { "val", "case19", "param", 5 },
                { "x b \"case20\" c f y" }, { "val", "case20" },
                { "x b \"case21\" d g y" }, { "val", "case21", "value", true },
                { "x b \"case22\" d h y" }, { "val", "case22", "value", false },
                { "x b \"case23\" d y" }, { "val", "case23" },
                { "x b \"case24\" y" }, { "val", "case24" },
                { "x b c e y" }, { "param", 5},
                { "x b c f y" }, { },
                { "x b d g y" }, { "value", true },
                { "x b d h y" }, { "value", false },
                { "x b d y" }, { },
                { "x b y" }, { },
        };

        SynParser synParser = createParser("SynParserSimpleTest_ComplexEmbeddedElements_grammar.txt");
        for (int pos = 0; pos < testData.length; pos += 2) {
            Object[] textArray = testData[pos];
            Object[] valuesArray = testData[pos + 1];
            String text = (String) textArray[0];
            checkComplexEmbeddedElements(synParser, valuesArray, text);
        }
    }

    private void checkComplexEmbeddedElements(SynParser synParser, Object[] valuesArray, String text)
            throws SynException
    {
        SynNode tree = parseStr(synParser, text, "file");
        ObjectNode objectNode = (ObjectNode) tree;

        int valuesCount = valuesArray.length / 2;
        assertEquals(valuesCount, objectNode.size());
        for (int i = 0; i < valuesCount; ++i) {
            String key = (String) valuesArray[i * 2];
            Object value = valuesArray[i * 2 + 1];
            if (value instanceof String) {
                assertEquals(value, objectNode.getString(key));
            } else if (value instanceof Integer) {
                assertEquals((int) (Integer) value, objectNode.getInt(key));
            } else if (value instanceof Boolean) {
                assertEquals((boolean) (Boolean) value, objectNode.getBoolean(key));
            } else {
                throw new IllegalStateException();
            }
        }
    }

    @Test
    public void testResultInNestedElement() throws SynException {
        try {
            createParserStr("@A : ( result=ID | name=STRING );");
            fail();
        } catch (SynGrammarException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(
                    "Special attribute 'result' cannot be used in nested productions"));
        }
    }

    @Test
    public void testResultInOptionalElement() throws SynException {
        try {
            createParserStr("@A : ( result=ID | name=STRING )?;");
            fail();
        } catch (SynGrammarException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(
                    "Special attribute 'result' cannot be used in nested productions"));
        }
    }

    @Test
    public void testDuplicatedKeysInParallelEmbeddedProductions() throws SynException {
        String[] grammars = {
                "@A : 'a' ('b' val=<1> | 'c' val=<2>);",
                "@A : 'a' ('b' val=<1> | 'c' val=<2>)?;",
                "@A : 'a' ('b' (foo=<1> | bar=<2>) | 'c' (foo=<3> | bar=<4>)?);",
                "@A : 'a' ('b' (foo=<1> | bar=<2>)? | 'c' (foo=<3> | bar=<4>))?;",
        };
        for (String grammar : grammars) {
            String grammar2 = grammar.replace('\'', '"');
            createParserStr(grammar2);
        }
    }

    @Test
    public void testDuplicatedKeysInNonparallelEmbeddedProductions() throws SynException {
        String[] grammars = {
                "@A : 'a' val=<1> ('b' foo=<0> | 'c' val=<2>);",
                "@A : 'a' ('b' foo=<0> | 'c' val=<2>) val=<1>;",
                "@A : 'a' val=<1> ('b' foo=<0> | 'c' val=<2>)?;",
                "@A : 'a' ('b' foo=<0> | 'c' val=<2>)? val=<1>;",

                "@A : ('x' | 'a' val=<1> ('b' foo=<0> | 'c' val=<2>));",
                "@A : ('x' | 'a' ('b' foo=<0> | 'c' val=<2>) val=<1>);",
                "@A : ('x' | 'a' val=<1> ('b' foo=<0> | 'c' val=<2>)?);",
                "@A : ('x' | 'a' ('b' foo=<0> | 'c' val=<2>)? val=<1>);",

                "@A : 'a' ('b' foo=<1> | 'c' bar=<2>)? ('d' foo=<3> | 'e' val=<4>);",
        };
        for (String grammar : grammars) {
            String grammar2 = grammar.replace('\'', '"');
            try {
                createParserStr(grammar2);
                fail();
            } catch (SynGrammarException e) {
                assertTrue(e.getMessage(), e.getMessage().contains("Duplicated attribute: "));
            }
        }
    }

    @Test
    public void testFloatingPointInGrammar() throws SynException {
        SynParser parser = createParserStr("@A : result=(INTEGER : '.')+ | 'kw' FLOAT;");
        try {
            parseStr(parser, "1.2.3.4.5", "A");
            fail();
        } catch (SynSyntaxException e) {
            // OK
        }
    }

    @Test
    public void testNoFloatingPointInGrammar() throws SynException {
        SynParser parser = createParserStr("@A : result=(INTEGER : '.')+;");
        SynNode node = parseStr(parser, "1.2.3.4.5", "A");
        ArrayNode arrayNode = (ArrayNode) node;
        assertEquals(5, arrayNode.size());
        for (SynNode subNode : arrayNode) {
            ValueNode valueNode = (ValueNode) subNode;
            assertEquals(TokenType.INTEGER, valueNode.getTokenDescriptor().getType());
        }
    }

    @Test
    public void testNegativeIntegerValueInGrammar() throws SynException {
        SynParser parser = createParserStr("@A : name=ID value=<-5>;");
        SynNode node = parseStr(parser, "Foo", "A");
        ObjectNode objectNode = (ObjectNode) node;
        assertEquals(-5, objectNode.getInt("value"));
    }

    @Test
    public void testInvlaidRecursiveGrammar() throws SynException {
        SynParser parser = createParserStr("@A : (B : '|')+; B : (C)+; C : ID | (B : '|')+ ;");
        parseStr(parser, "a | b | c", "A");
    }

    static SynParser createParser(String grammarPath) throws SynException {
        return new SynParser(SynParserSimpleTest.class, grammarPath);
    }

    static SynNode parse(SynParser parser, String textPath, String startNt) throws SynException {
        SynResult synResult = parser.parse(startNt, SynParserSimpleTest.class, textPath);
        return synResult.getRootNode();
    }

    static SynParser createParserStr(String grammarStr) throws SynException {
        return new SynParser(grammarStr);
    }

    static SynNode parseStr(SynParser parser, String str, String startNt) throws SynException {
        SynResult synResult = parser.parse(startNt, str);
        return synResult.getRootNode();
    }

    static Reader openResource(String name) throws IOException {
        return CommonUtil.openResourceReader(SynParserSimpleTest.class, name);
    }
}
