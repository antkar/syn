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
package org.antkar.syn.internal.parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antkar.syn.SynAmbiguityException;
import org.antkar.syn.SynSyntaxException;
import org.antkar.syn.TextPos;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.lrtables.ParserShift;
import org.antkar.syn.internal.lrtables.ParserState;

/**
 * Some helper methods.
 */
final class ParserEngineHelper {
    private ParserEngineHelper(){}
    
    /**
     * Returns the collection of tokens which are allowed by the specified parser stacks. Used to construct a
     * {@link SynSyntaxException}.
     */
    static Collection<TokenDescriptor> getExpectedTokens(List<ParserStack> stacks) {
        Set<TokenDescriptor> result = new HashSet<>();
        for (ParserStack stack : stacks) {
            ParserState state = stack.getTop().getState();
            for (ParserShift shift : state.getShifts()) {
                TokenDescriptor descriptor = shift.tokenDescriptor;
                result.add(descriptor);
            }
        }
        return result;
    }
    
    /**
     * Creates an instance of {@link SynAmbiguityException}. The message of the created exception contains
     * the dump of parser trees that were in a conflict.
     */
    static SynAmbiguityException createAmbiguityException(TextPos textPos, ParserStack stack1, ParserStack stack2) {
        final String charset = "UTF-8";
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        PrintStream out;
        try {
            out = new PrintStream(byteOut, false, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        printAmbiguityMessage(stack1, stack2, out);
        
        out.flush();
        byte[] data = byteOut.toByteArray();
        String message = new String(data, Charset.forName(charset));
        
        return new SynAmbiguityException(textPos, message);
    }

    private static void printAmbiguityMessage(ParserStack stack1, ParserStack stack2, PrintStream out) {
        out.println("Ambiguity detected");
        out.println("Tree 1:");
        printStackDiff(out, stack1, stack2);
        out.println("Tree 2:");
        printStackDiff(out, stack2, stack1);
    }
    
    /**
     * Prints trees of differing stack elements.
     */
    private static void printStackDiff(PrintStream out, ParserStack stack1, ParserStack stack2) {
        ParserStackElement top1 = stack1.getTop();
        ParserStackElement top2 = stack2.getTop();
        
        assert top1.getDepth() == top2.getDepth();
        
        while (top1 != top2) {
            top1.print(out, 1);
            top1 = top1.getPrev();
            top2 = top2.getPrev();
        }
    }
}
