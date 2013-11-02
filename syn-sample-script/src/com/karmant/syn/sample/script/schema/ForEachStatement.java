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

import com.karmant.syn.StringToken;
import com.karmant.syn.SynField;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.StatementResult;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.LValue;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * For-each statement syntax node.
 */
public class ForEachStatement extends ForStatement {
	/** Not <code>null</code> if a new control variable is declared in the header of the
	 * <code>for</code> statement. */
	@SynField
	private String synNewVariable;
	
	/** The name of the control variable. */
	@SynField
	private StringToken synVariable;
	
	/** The expression to be iterated. */
	@SynField
	private Expression synExpression;
	
	/** The statement to be executed. */
	@SynField
	private Statement synStatement;

	public ForEachStatement(){}

	@Override
	StatementResult execute0(ScriptScope scope) throws SynsException {
		//Define the control variable.
		final ScriptScope innerScope = scope.deriveLoopScope("for");
		final LValue variable = getControlVariable(scope, innerScope);
		
		//Evaluate the expression.
		Value value = synExpression.evaluate(scope);
		RValue rvalue = value.toRValue();
		
		//Iterate over values.
		for (RValue element : rvalue.toIterable()) {
			variable.assign(element);
			StatementResult result = synStatement.execute(innerScope);
			if (result.isBreak()) {
				break;
			} else if (result.isReturn()) {
				return result;
			}
		}
		
		return StatementResult.NONE;
	}

	/**
	 * Returns the control variable for this <code>for</code> statement. Either creates a new
	 * variable, or looks for an existing one.
	 */
	private LValue getControlVariable(ScriptScope scope, final ScriptScope innerScope) throws SynsException {
		Value variable;
		if (synNewVariable != null) {
			variable = Value.newVariable(Value.forNull());
			innerScope.addValue(synVariable, variable);
		} else {
			variable = scope.getValue(synVariable);
		}
		
		final LValue lvariable = variable.toLValue();
		return lvariable;
	}
}
