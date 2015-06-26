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

import org.antkar.syn.SynBinder;
import org.antkar.syn.SynParser;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Light {@link SynParser} test suite. Does not include {@link SynBinder}-related tests.
 */
@RunWith(Suite.class)
@SuiteClasses({
    TokenStreamTest.class,
    XmlGrammarParserTest.class,
    EbnfToBnfConverterTest.class,
    ParserConfiguratorTest.class,
    ParserEngineTest.class,
    SynParserSimpleTest.class,
    SynParserJavaTest.class,
    TokenStreamNumberTest.class,
    TokenStreamStringTest.class,
    BugsTest.class,
})
public class LightSuite {
    private LightSuite(){}
}
