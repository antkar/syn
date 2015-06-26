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
package com.karmant.syn.sample.script.rt;

import com.karmant.syn.sample.script.rt.op.operand.Operand;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Type descriptor for <code>double</code> type.
 */
public final class DoublePrimitiveTypeDescriptor extends PrimitiveTypeDescriptor {
    public static final PrimitiveTypeDescriptor INSTANCE = new DoublePrimitiveTypeDescriptor();
    
    private DoublePrimitiveTypeDescriptor(){}

    @Override
    public RValue cast(Operand operand) throws SynsException {
        double result = operand.castToDouble();
        return Value.forDouble(result);
    }
    
    @Override
    public String toString() {
        return "double";
    }
}
