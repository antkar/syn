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
package com.karmant.syn.sample.script.schema;

import java.util.List;

import com.karmant.syn.StringToken;
import com.karmant.syn.SynField;
import com.karmant.syn.TextPos;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.TextSynsException;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Script syntax node. Is the start nonterminal for the the Script Language grammar.
 */
public class Script {
	/** The root block. */
	@SynField
	private Block synBlock;
	
	public Script(){}
	
	/**
	 * Executes this script in the specified scope.
	 */
	public void execute(ScriptScope scope) throws SynsException {
		synBlock.execute(scope);
	}
	
	/**
	 * Executes a collection of scripts with the specified command line arguments. The arguments
	 * are accessible from the scripts through the predefined <code>args</code> array variable.
	 */
	public static void execute(List<Script> scripts, List<String> arguments) throws SynsException {
		if (scripts.isEmpty()) {
			return;
		}
		
		//Create the root scope.
		ScriptScope scope = ScriptScope.createRootScope();
		
		//Add command line arguments to the scope.
		registerCommandLineArguments(scope, arguments);
		
		//Add declarations declared in each script into the root scope.
		for (Script script : scripts) {
			script.synBlock.registerDeclarations(scope);
		}
		
		//Execute each script, one by one.
		for (Script script : scripts) {
			script.synBlock.executeWithoutDeclarations(scope);
		}
	}
	
	/**
	 * Puts command line arguments to the specified scope.
	 */
	private static void registerCommandLineArguments(ScriptScope scope, List<String> arguments)
			throws TextSynsException
	{
		StringToken name = new StringToken(TextPos.NULL, "args");
		
		RValue[] argValues = new RValue[arguments.size()];
		for (int i = 0; i < argValues.length; ++i) {
			argValues[i] = Value.forString(arguments.get(i));
		}
		
		RValue argsValue = Value.newArray(argValues);
		scope.addValue(name, argsValue);
	}
	
	@Override
	public String toString() {
		return synBlock + "";
	}
}
