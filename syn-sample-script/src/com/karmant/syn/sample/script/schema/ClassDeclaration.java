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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.karmant.syn.StringToken;
import com.karmant.syn.SynField;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.TextSynsException;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Class declaration syntax node.
 */
public class ClassDeclaration extends Declaration {
	/** Member declarations. */
	@SynField
	private ClassMemberDeclaration[] synMembers;
	
	public ClassDeclaration(){}

	@Override
	Value evaluateValue(ScriptScope scope) throws SynsException {
		checkNameConflicts();
		
		//Divide members into categories.
		List<ConstantDeclaration> constants = new ArrayList<>();
		List<VariableDeclaration> variables = new ArrayList<>();
		List<FunctionDeclaration> functions = new ArrayList<>();
		for (ClassMemberDeclaration member : synMembers) {
			member.classify(constants, variables, functions);
		}
		
		//Create a class scope and put the declared constants there.
		Map<String, RValue> constantValues = new HashMap<>();
		ScriptScope classScope = scope.deriveClassScope("class " + getName(), false);
		setupClassScope(constants, constantValues, classScope);
		
		//Find the constructor declaration, if any.
		String className = getName();
		FunctionDeclaration constructor = findConstructor(functions);
		
		//Create the list of instance members declarations.
		List<Declaration> instanceMembers = new ArrayList<>();
		instanceMembers.addAll(variables);
		instanceMembers.addAll(functions);
		
		//Create a class value.
		Value value = Value.forClass(className, classScope, constructor, constantValues, instanceMembers);
		return value;
	}

	/**
	 * Checks if there are member name conflicts.
	 */
	private void checkNameConflicts() throws TextSynsException {
		Set<String> names = new HashSet<>();
		for (ClassMemberDeclaration member : synMembers) {
			StringToken nameTk = member.getDeclaration().getNameTk();
			String name = nameTk.getValue();
			if (!names.add(name)) {
				throw new TextSynsException("Duplicated class member: " + name, nameTk.getPos());
			}
		}
	}

	/**
	 * Finds the constructor function declaration. A constructor is the function with the same
	 * name as the name of the class.
	 */
	private FunctionDeclaration findConstructor(List<FunctionDeclaration> functions) {
		FunctionDeclaration constructor = null;
		
		String className = getName();
		for (FunctionDeclaration function : functions) {
			if (className.equals(function.getName())) {
				constructor = function;
				functions.remove(function);
				break;
			}
		}
		
		return constructor;
	}

	/**
	 * Initializes class scope by putting constants there.
	 */
	private void setupClassScope(
			List<ConstantDeclaration> constants,
			Map<String, RValue> constantValues,
			ScriptScope classScope) throws SynsException
	{
		for (ConstantDeclaration constant : constants) {
			Value value = constant.addToScope(classScope);
			RValue rvalue = value.toRValue(); 
			constantValues.put(constant.getName(), rvalue);
		}
	}
	
	@Override
	void classify(
			List<ConstantDeclaration> constants,
			List<VariableDeclaration> variables,
			List<FunctionDeclaration> functions) throws SynsException
	{
		throw new SynsException("Nested classes are not supported");
	}
	
	@Override
	public String toString() {
		return "class " + getName();
	}
}
