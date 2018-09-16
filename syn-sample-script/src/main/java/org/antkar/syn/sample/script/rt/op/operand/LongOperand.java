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
package org.antkar.syn.sample.script.rt.op.operand;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.RValue;

/**
 * Operand of type <code>long</code>.
 */
final class LongOperand extends Operand {
    private final RValue rValue;
    private final long value;

    LongOperand(RValue rValue, long value) {
        this.rValue = rValue;
        this.value = value;
    }

    @Override
    public OperandType getType() {
        return OperandType.LONG;
    }

    @Override
    public RValue toRValue() {
        return rValue;
    }

    @Override
    public boolean booleanValueImplicit() throws SynsException {
        return true;
    }

    @Override
    public long longValue() throws SynsException {
        return value;
    }

    @Override
    public double doubleValue() throws SynsException {
        return value;
    }

    @Override
    public String stringValue() throws SynsException {
        return value + "";
    }

    @Override
    public Object objectValue() throws SynsException {
        return value;
    }

    @Override
    public int castToInt() throws SynsException {
        return (int)value;
    }

    @Override
    public long castToLong() throws SynsException {
        return value;
    }

    @Override
    public double castToDouble() throws SynsException {
        return value;
    }
}
