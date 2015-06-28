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
package org.antkar.syn.sample.script.rt.op;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.rt.value.RValue;

/**
 * Script Language <code>||</code> operator.
 */
class OrBinaryOperator extends BinaryOperator {
    OrBinaryOperator() {
        super("||");
    }

    @Override
    public RValue evaluate(Operand left) throws SynsException {
        boolean b = left.booleanValueImplicit();
        return b ? left.toRValue() : null;
    }
    
    @Override
    public RValue evaluate(Operand left, Operand right) throws SynsException {
        boolean b = left.booleanValueImplicit();
        return b ? left.toRValue() : right.toRValue();
    }
}
