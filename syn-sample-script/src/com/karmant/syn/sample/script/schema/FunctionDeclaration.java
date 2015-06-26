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
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.StatementResult;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Script function declaration syntax node.
 */
public class FunctionDeclaration extends Declaration {
    /** Formal parameters. */
    @SynField
    private StringToken[] synParameters;
    
    /** The body. */
    @SynField
    private Block synBlock;

    public FunctionDeclaration(){}
    
    @Override
    Value evaluateValue(ScriptScope scope) throws SynsException {
        return Value.forFunction(scope, this);
    }
    
    @Override
    void classify(
            List<ConstantDeclaration> constants,
            List<VariableDeclaration> variables,
            List<FunctionDeclaration> functions)
    {
        functions.add(this);
    }
    
    /**
     * Calls this function with the specified actual arguments.
     */
    public Value call(ScriptScope scope, RValue[] arguments) throws SynsException {
        ScriptScope argumentsScope = scope.deriveFunctionScope("function " + getName());
        
        for (int i = 0; i < synParameters.length; ++i) {
            StringToken param = synParameters[i];
            Value value = i < arguments.length ? arguments[i] : Value.forNull();
            Value variable = Value.newVariable(value);
            argumentsScope.addValue(param, variable);
        }
        
        StatementResult result = synBlock.execute(argumentsScope);
        return result.isReturn() ? result.getReturnValue() : Value.forVoid();
    }
    
    @Override
    boolean isFunction() {
        return true;
    }
    
    @Override
    public String toString() {
        return "function " + getName();
    }
}
