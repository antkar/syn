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

/**
 * Primitive type descriptor used by Script Language explicit cast operator to denote a type.
 */
public abstract class PrimitiveTypeDescriptor {
    PrimitiveTypeDescriptor(){}

    /**
     * Casts the specified operand to the primitive type denoted by this type descriptor.
     * 
     * @param operand the operand.
     * @return the casted value.
     * @throws SynsException if the operand cannot be casted to the type.
     */
    public abstract RValue cast(Operand operand) throws SynsException;
}
