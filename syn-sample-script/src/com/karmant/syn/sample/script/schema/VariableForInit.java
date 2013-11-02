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

import com.karmant.syn.SynField;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.util.MiscUtil;

/**
 * New variable <code>for</code> initializer.
 */
public class VariableForInit extends ForInit {
	/** List of variables. */
	@SynField
	private ForVariableDeclaration[] synVariables;

	public VariableForInit(){}

	@Override
	void execute(ScriptScope scope) throws SynsException {
		for (ForVariableDeclaration declaration : synVariables) {
			declaration.addToScope(scope);
		}
	}
	
	@Override
	public String toString() {
		return MiscUtil.arrayToString(synVariables, ", ");
	}
}
