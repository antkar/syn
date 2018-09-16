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
package org.antkar.syn.sample.script.rt.value;

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.javacls.TypeMatchPrecision;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.schema.FunctionObject;

/**
 * Script function value.
 */
final class FunctionValue extends RValue implements AdaptableToJavaInterface {
    /** The scope where the function was declared. The function will be executed in that scope. */
    private final ScriptScope scope;

    /** The function object. */
    private final FunctionObject function;

    FunctionValue(ScriptScope scope, FunctionObject function) {
        this.scope = scope;
        this.function = function;
    }

    @Override
    public Object toJava(Class<?> type, TypeMatchPrecision precision) throws SynsException {
        return BlockToJavaAdapter.toJava(this, type);
    }

    @Override
    public Value call(RValue[] arguments) throws SynsException {
        return function.call(scope, arguments);
    }

    @Override
    public boolean hasFunction(String name) {
        return false;
    }

    @Override
    public Value callFunction(String name, RValue[] arguments) throws SynsException {
        throw new IllegalStateException(name);
    }

    @Override
    public Operand toOperand() throws SynsException {
        return Operand.forObject(this, function);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.FUNCTION;
    }

    @Override
    public String getTypeMessage() {
        return getCompoundTypeMessage(function.getScopeName());
    }

    @Override
    public String toString() {
        return getTypeMessage();
    }
}
