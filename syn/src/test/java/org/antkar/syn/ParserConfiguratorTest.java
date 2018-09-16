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

import java.net.URL;

import org.antkar.syn.internal.bnf.BnfGrammar;
import org.antkar.syn.internal.ebnf.EbnfGrammar;
import org.antkar.syn.internal.grammar.EbnfToBnfConverter;
import org.antkar.syn.internal.grammar.XmlGrammarParser;
import org.antkar.syn.internal.lrtables.ParserConfiguration;
import org.antkar.syn.internal.lrtables.ParserConfigurator;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ParserConfigurator}.
 */
public class ParserConfiguratorTest extends Assert {
    @Test
    public void testSuccess() throws Exception {
        URL url = XmlGrammarParser.class.getResource("syn_grammar.xml");
        EbnfGrammar eGrammar = XmlGrammarParser.parseGrammar(url);
        BnfGrammar bGrammar = EbnfToBnfConverter.convert(eGrammar);

        ParserConfiguration config = ParserConfigurator.makeConfiguration(bGrammar);
        assertNotNull(config);
    }
}
