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
 * Unit tests for using {@link SynParser} with Java Language grammar.
 */
public class SynParserJavaTest extends TestCase {
    @Test
    public void testConstructSuccess() throws Exception {
        SynParserSimpleTest.createParser("SynParserJavaTest_grammar.txt");
    }
    
    @Test
    public void testParseSuccess0() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_0_text.txt");
    }
    
    @Test
    public void testParseSuccess1() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_1_text.txt");
    }
    
    @Test
    public void testParseSuccess2() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_2_text.txt");
    }
    
    @Test
    public void testParseSuccess3() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_3_text.txt");
    }
    
    @Test
    public void testParseSuccess4() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_4_text.txt");
    }
    
    @Test
    public void testParseSuccess5() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_5_text.txt");
    }
    
    @Test
    public void testParseSuccess6() throws Exception {
        parse("SynParserJavaTest_ParseSuccess_6_text.txt");
    }
    
    private void parse(String fileName) throws SynException {
        SynParser synParser = SynParserSimpleTest.createParser("SynParserJavaTest_grammar.txt");
        synParser.setFailOnAmbiguity(true);
        SynParserSimpleTest.parse(synParser, fileName, "goal");
    }
}
