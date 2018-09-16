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
package org.antkar.syn.binder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.StringSourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.binder.schema.bug001.Bug001Foo;
import org.antkar.syn.binder.schema.bug001.Bug001Schema;
import org.antkar.syn.binder.schema.bug002.Bug002Foo;
import org.antkar.syn.binder.schema.bug002.Bug002Schema;
import org.antkar.syn.binder.schema.bug003.Bug003Schema;
import org.antkar.syn.binder.schema.result_key.ResultKeyBar;
import org.antkar.syn.binder.schema.result_key.ResultKeyFoo;
import org.antkar.syn.binder.schema.result_key.ResultKeyObj;
import org.antkar.syn.binder.schema.result_key.ResultKeySchema;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link SynBinder}.
 */
public class SynBinderTest extends Assert {

    @Test
    public void testResultKeyPresentInGrammar() throws SynException, IOException {
        String text = "Hello";
        ResultKeySchema schema = parse(ResultKeySchema.class, "result_key_grammar.txt", text);
        ResultKeyObj obj = schema.getObj();
        assertNotNull(obj);
        ResultKeyFoo foo = (ResultKeyFoo) obj;
        assertEquals("Hello", foo.getName());
    }

    @Test
    public void testResultKeyRuleUsedInText() throws SynException, IOException {
        String text = "(12345)";
        ResultKeySchema schema = parse(ResultKeySchema.class, "result_key_grammar.txt", text);
        ResultKeyObj obj = schema.getObj();
        assertNotNull(obj);
        ResultKeyBar bar = (ResultKeyBar) obj;
        assertEquals(12345, bar.getValue());
    }

    @Test
    public void testResultKeyInChildElement() throws SynException {
        String grammar =
                "@Object : ID ( result=String | ID ); " +
                "String : \"aaa\"; ";
        try {
            createBinderStr(Object.class, grammar);
            fail();
        } catch (SynBinderException e) {
            assertEquals("Special name 'result' cannot be used as an attribute", e.getMessage());
        }
    }

    @Test
    public void testFieldIsBoundInEmbeddedElementOnly() throws SynException, IOException {
        SynBinder<Bug001Schema> binder = createBinder(Bug001Schema.class, "bug001_grammar.txt");

        Bug001Schema schema = parseStr(binder, "aaa : bbb");
        Bug001Foo foo = schema.getFoo();
        assertEquals("aaa", foo.getName());
        assertEquals("bbb", foo.getType());
    }

    @Test
    public void testFieldIsBoundInEmbeddedElementOnlyButNotSet() throws SynException, IOException {
        SynBinder<Bug001Schema> binder = createBinder(Bug001Schema.class, "bug001_grammar.txt");

        Bug001Schema schema = parseStr(binder, "aaa");
        Bug001Foo foo = schema.getFoo();
        assertEquals("aaa", foo.getName());
        assertEquals(null, foo.getType());
    }

    @Test
    public void testValuesInEmbeddedElement() throws SynException, IOException {
        SynBinder<Bug002Schema> binder = createBinder(Bug002Schema.class, "bug002_grammar.txt");

        Bug002Schema schema = parseStr(binder, "aaa ; bbb? ; ccc+ ; ddd* ;");

        Bug002Foo[] foos = schema.getFoos();
        assertEquals(4, foos.length);

        Bug002Foo foo0 = foos[0];
        assertEquals("aaa", foo0.getName());
        assertEquals(0, foo0.getCardinality());

        Bug002Foo foo1 = foos[1];
        assertEquals("bbb", foo1.getName());
        assertEquals(5, foo1.getCardinality());

        Bug002Foo foo2 = foos[2];
        assertEquals("ccc", foo2.getName());
        assertEquals(10, foo2.getCardinality());

        Bug002Foo foo3 = foos[3];
        assertEquals("ddd", foo3.getName());
        assertEquals(15, foo3.getCardinality());
    }

    @Test
    public void testIntegerInRepetitionElement() throws SynException, IOException {
        SynBinder<Bug003Schema> binder = createBinder(Bug003Schema.class, "bug003_grammar.txt");

        Bug003Schema schema = parseStr(binder, "Foo 1,2,3,4,5");
        int[] number = schema.getNumber();
        assertEquals("[1, 2, 3, 4, 5]", Arrays.toString(number));
    }

    static <T> T parse(Class<T> cls, String grammarFile, String text)
            throws SynException, IOException
    {
        SynBinder<T> binder = createBinder(cls, grammarFile);
        StringReader reader = new StringReader(text);
        SourceDescriptor sourceDescriptor = new StringSourceDescriptor("text");
        T result = binder.parse(reader, sourceDescriptor);
        return result;
    }

    static <T> SynBinder<T> createBinder(Class<T> cls, String grammarPath)
            throws IOException, SynException
    {
        try (
                InputStream in = cls.getResourceAsStream(grammarPath);
                Reader reader = new InputStreamReader(in))
        {
            SynBinder<T> binder = new SynBinder<>(cls, reader);
            return binder;
        }
    }

    private static <T> SynBinder<T> createBinderStr(Class<T> cls, String grammarStr)
            throws SynException
    {
        Reader reader = new StringReader(grammarStr);
        return new SynBinder<>(cls, reader);
    }

    private static <T> T parseStr(SynBinder<T> binder, String textStr) throws SynException {
        Reader reader = new StringReader(textStr);
        return binder.parse(reader);
    }
}
