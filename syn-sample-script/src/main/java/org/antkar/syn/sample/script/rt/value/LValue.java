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
import org.antkar.syn.sample.script.rt.op.operand.Operand;

/**
 * An l-value - a value which actual value is variable and can be changed.
 */
public abstract class LValue extends Value {
    LValue(){}

    @Override
    public final LValue toLValue() {
        return this;
    }

    @Override
    public Operand toOperand() throws SynsException {
        return toRValue().toOperand();
    }

    @Override
    public final Value getMemberOpt(String name, ScriptScope readerScope) throws SynsException {
        RValue rvalue = toRValue();
        return rvalue.getMemberOpt(name, readerScope);
    }

    @Override
    public final Value call(RValue[] arguments) throws SynsException {
        RValue rvalue = toRValue();
        return rvalue.call(arguments);
    }

    /**
     * Assigns a new actual value to this value.
     */
    public abstract void assign(RValue value) throws SynsException;
}
