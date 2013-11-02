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
import com.karmant.syn.TextPos;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;
import com.karmant.syn.sample.script.util.MiscUtil;

/**
 * Script function expression syntax node.
 */
public class FunctionExpression extends TerminalExpression {
	/** The expression which has to be treated as a function. */
	@SynField
	private Expression synFunction;
	
	/** Actual arguments. */
	@SynField
	private Expression[] synArguments;

	public FunctionExpression(){}
	
	@Override
	TextPos getStartTextPos() {
		return synFunction.getStartTextPos();
	}

	@Override
	Value evaluate0(ScriptScope scope) throws SynsException {
		Value functionValue = synFunction.evaluate(scope);
		
		RValue[] argumentValues = new RValue[synArguments.length];
		for (int i = 0; i < argumentValues.length; ++i) {
			Value argumentValue = synArguments[i].evaluate(scope);
			argumentValues[i] = argumentValue.toRValue();
		}
		
		return functionValue.call(argumentValues);
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append(synFunction);
		bld.append("( ");
		MiscUtil.appendArray(bld, synArguments, ", ");
		bld.append(" )");
		return bld + "";
	}
}
