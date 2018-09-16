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

import org.antkar.syn.internal.scanner.DefaultTokenStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for some found bugs.
 */
public final class BugsTest extends Assert {
    @Test
    public void testIdentifierAtEndOfFile() throws SynException {
        String source = "aaa";
        DefaultTokenStream tokenStream = TokenStreamNumberTest.createTokenStream(source);
        tokenStream.nextToken();
        assertEquals(TokenType.ID, tokenStream.getTokenDescriptor().getType());
        tokenStream.nextToken();
        assertEquals(TokenType.END_OF_FILE, tokenStream.getTokenDescriptor().getType());
    }

    /*
     * Null value was not properly supported. Productions like "P: result=<null>" didn't return
     * a null value, but its string representation.
     */
    @Test
    public void testNullValue() throws SynException {
        SynParser parser = SynParserSimpleTest.createParserStr("@File: ID result=<null>;");
        SynNode node = SynParserSimpleTest.parseStr(parser, "aaa", "File");
        assertEquals(null, node);
    }

    @Test
    public void testNpeInArrayNode() throws SynException {
        SynParser parser = SynParserSimpleTest.createParserStr("@File: (Rule)*; Rule: ('!')? ID;");
        SynNode node = SynParserSimpleTest.parseStr(parser, "aaa !bbb ccc", "File");
        assertEquals("[\"!\"]", node.toString());
    }
}
