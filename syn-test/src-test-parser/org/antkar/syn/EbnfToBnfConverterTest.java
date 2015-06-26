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
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.antkar.syn.BnfGrammar;
import org.antkar.syn.EbnfGrammar;
import org.antkar.syn.EbnfToBnfConverter;
import org.antkar.syn.StringSourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.SynGrammarParser;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.XmlGrammarParser;
import org.junit.Test;

/**
 * Unit test for {@link EbnfToBnfConverter}.
 */
public class EbnfToBnfConverterTest extends TestCase {
    @Test
    public void testConvertSuccess() throws Exception {
        URL url = getClass().getResource("syn_grammar.xml");
        EbnfGrammar eGrammar = XmlGrammarParser.parseGrammar(url);
        BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);
        assertNotNull(bGrammar);
    }
    
    @Test
    public void testUnreferencedKeywordsAreKept() throws SynException {
        Reader grammarReader = new StringReader("@goal : ; foo : 'aaa' | 'bbb' ;");
        EbnfGrammar eGrammar =
                SynGrammarParser.parseGrammar(grammarReader, new StringSourceDescriptor("<grammar>"));
        BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);
        
        List<TokenDescriptor> tokens = bGrammar.getTokens();
        assertEquals(2, tokens.size());
        assertEquals("aaa", tokens.get(0).getLiteral());
        assertEquals("bbb", tokens.get(1).getLiteral());
    }
    
    @Test
    public void testUnreferencedKeycharsAreKept() throws SynException {
        Reader grammarReader = new StringReader("@goal : ; foo : '+' | '-' ;");
        EbnfGrammar eGrammar =
                SynGrammarParser.parseGrammar(grammarReader, new StringSourceDescriptor("<grammar>"));
        BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);
        
        List<TokenDescriptor> tokens = bGrammar.getTokens();
        assertEquals(2, tokens.size());
        assertEquals("+", tokens.get(0).getLiteral());
        assertEquals("-", tokens.get(1).getLiteral());
    }
}