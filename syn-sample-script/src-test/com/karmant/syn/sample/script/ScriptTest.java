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
package com.karmant.syn.sample.script;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.Assert;

import com.karmant.syn.StringToken;
import com.karmant.syn.SynException;
import com.karmant.syn.TextPos;
import com.karmant.syn.sample.script.ScriptExecutor;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.util.NullInputStream;

/**
 * Common superclass for Script Language tests. Tests complete script execution.
 */
public abstract class ScriptTest extends Assert {
    
    private String stdOut;
    private String stdErr;

    /**
     * Executes the given script, redirecting the output to {@link #stdOut} and {@link #stdErr} fields.
     */
    void execute(String script) throws SynException, SynsException {
        //Create stdout redirection stream.
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        @SuppressWarnings("resource")
        PrintStream out = createPrintStream(bout);
        
        //Create stderr redirection stream.
        ByteArrayOutputStream berr = new ByteArrayOutputStream();
        @SuppressWarnings("resource")
        PrintStream err = createPrintStream(berr);
        
        //Add test API to the root scope.
        ScriptScope scope = ScriptScope.createRootScope();
        scope.addOnDemandImport(stringToNameChain(API.class.getCanonicalName()));
        
        //Execute the script.
        try {
            ScriptExecutor.execute(scope, script, NullInputStream.INSTANCE, out, err);
        } finally {
            stdOut = getPrintedString(bout);
            stdErr = getPrintedString(berr);
        }
    }
    
    /**
     * Creates a name chain from a fully qualified Java class name.
     */
    private static StringToken[] stringToNameChain(String str) {
        String[] parts = str.split("[.]");
        StringToken[] result = new StringToken[parts.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new StringToken(TextPos.NULL, parts[i]);
        }
        return result;
    }

    /**
     * Checks if the standard output matches the specified value, and the standard error stream
     * is empty.
     */
    void chkOut(String out) {
        chkOut(out, "");
    }
    
    /**
     * Checks standard output and standard error streams.
     */
    void chkOut(String out, String err) {
        assertEquals(out, stdOut);
        assertEquals(err, stdErr);
    }
    
    private static final Charset CHARSET = Charset.forName("UTF-8");
    
    private static PrintStream createPrintStream(OutputStream out) {
        try {
            return new PrintStream(out, false, CHARSET.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String getPrintedString(ByteArrayOutputStream bout) {
        byte[] data = bout.toByteArray();
        return new String(data, CHARSET);
    }
    
    /**
     * Test API - methods of this class are accessible from test scripts.
     */
    public static final class API {
        private API(){}
        
        public static void print(Object value) {
            System.out.print(value + " ");
        }
    }
}
