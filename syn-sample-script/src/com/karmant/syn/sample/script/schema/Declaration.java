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
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Declaration syntax node.
 */
public abstract class Declaration {
	/** The text position of the first token. */
	@SynField
	private TextPos synPos;

	/** The name of the declaration. */
	@SynField
	private StringToken synName;

	public Declaration(){}
	
	/**
	 * Returns the text position of the first token of this declaration.
	 */
	TextPos getStartTextPos() {
		return synPos;
	}

	/**
	 * Returns the name of the declaration.
	 */
	public String getName() {
		return synName.getValue();
	}
	
	/**
	 * Returns the name token of the declaration.
	 */
	public StringToken getNameTk() {
		return synName;
	}

	/**
	 * Adds this declaration to the specified scope.
	 */
	public final Value addToScope(ScriptScope scope) throws SynsException {
		Value value = evaluateValue(scope);
		scope.addValue(synName, value);
		return value;
	}
	
	/**
	 * Evaluates the initial value of the declaration.
	 */
	abstract Value evaluateValue(ScriptScope scope) throws SynsException;
	
	/**
	 * Returns <code>true</code> if this declaration is a function.
	 */
	boolean isFunction() {
		return false;
	}

	/**
	 * Classifies this declaration as either a constant, a variable or a function.
	 */
	abstract void classify(
			List<ConstantDeclaration> constants,
			List<VariableDeclaration> variables,
			List<FunctionDeclaration> functions) throws SynsException;
}
