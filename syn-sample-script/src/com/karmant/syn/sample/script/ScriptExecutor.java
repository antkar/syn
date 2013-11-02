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

import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;

import com.karmant.syn.SynBinder;
import com.karmant.syn.SynException;
import com.karmant.syn.TokenStream;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.schema.Script;

/**
 * Encapsulates scripting functionality. Allows to execute an arbitrary script.
 */
public final class ScriptExecutor {
	private static final SynBinder<Script> BINDER;
	private static final ScannerFactory SCANNER_FACTORY;

	static {
		try {
			BINDER = new SynBinder<>(Script.class, Script.class, "Script_grammar.txt");
		} catch (SynException e) {
			throw new IllegalStateException(e);
		}
		SCANNER_FACTORY = new BinderScannerFactory();
	}
	
	private ScriptExecutor(){}
	
	/**
	 * Executes a script.
	 * 
	 * @param scope the root scope.
	 * @param source the script source code.
	 * @throws SynException if a syntax error occurs.
	 * @throws SynsException if a script run-time error occurs.
	 */
	public static void execute(ScriptScope scope, String source) throws SynException, SynsException {
		Script script = BINDER.parse(source);
		script.execute(scope);
	}

	/**
	 * Executes a script with standard input and output streams redirection.
	 * 
	 * @param scope the root scope.
	 * @param source the script source code.
	 * @param stdin the stream where to redirect the standard input from.
	 * @param stdout the stream where to redirect the standard output to.
	 * @param stderr the stream where to redirect the standard error to.
	 * @throws SynException if a syntax error occurs.
	 * @throws SynsException if a script run-time error occurs.
	 */
	public static void execute(
			ScriptScope scope,
			String source,
			InputStream stdin,
			PrintStream stdout,
			PrintStream stderr) throws SynException, SynsException
	{
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		PrintStream oldErr = System.err;
		
		try {
			System.setIn(stdin);
			System.setOut(stdout);
			System.setErr(stderr);
			execute(scope, source);
		} finally {
			System.setIn(oldIn);
			System.setOut(oldOut);
			System.setErr(oldErr);
		}
	}
	
	/**
	 * Returns the {@link SynBinder} instance that can be used to parse a {@link Script}.
	 * @return the {@link SynBinder} instance.
	 */
	public static SynBinder<Script> getSynBinder() {
		return BINDER;
	}
	
	/**
	 * Returns the scanner factory for the Script Language grammar.
	 * @return the scanner factory.
	 */
	public static ScannerFactory getScannerFactory() {
		return SCANNER_FACTORY;
	}
	
	/**
	 * {@link SynBinder}-based scanner factory.
	 */
	private static final class BinderScannerFactory implements ScannerFactory {
		BinderScannerFactory(){}

		@Override
		public TokenStream createTokenStream(String string) {
			Reader reader = new StringReader(string);
			try {
				return BINDER.createTokenStream(null, reader);
			} catch (SynException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
