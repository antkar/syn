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
package com.karmant.syn.sample.script.rt.value;

import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.op.operand.Operand;
import com.karmant.syn.sample.script.schema.FunctionDeclaration;

/**
 * Script function value.
 */
class FunctionValue extends RValue {
    /** The scope where the function was declared. The function will be executed in that scope. */
    private final ScriptScope scope;
    
    /** The function declaration. */
    private final FunctionDeclaration function;
    
    FunctionValue(ScriptScope scope, FunctionDeclaration function) {
        this.scope = scope;
        this.function = function;
    }
    
    @Override
    public Value call(RValue[] arguments) throws SynsException {
        return function.call(scope, arguments);
    }
    
    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forObject(function);
    }
    
    @Override
    public ValueType getValueType() {
        return ValueType.FUNCTION;
    }
    
    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(function.getName());
    }
}
