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
package org.antkar.syn.sample.script.schema;

import org.antkar.syn.sample.script.rt.DoublePrimitiveTypeDescriptor;
import org.antkar.syn.sample.script.rt.IntPrimitiveTypeDescriptor;
import org.antkar.syn.sample.script.rt.LongPrimitiveTypeDescriptor;
import org.antkar.syn.sample.script.rt.PrimitiveTypeDescriptor;

import org.antkar.syn.SynField;
import org.antkar.syn.SynInit;

/**
 * Primitive type name syntax node. Used in an explicit type cast expression.
 */
public class PrimitiveType {
    /** The name of the primitive type. */
    @SynField
    private String synType;
    
    /** The descriptor of the specified primitive type. */
    private PrimitiveTypeDescriptor type;
    
    public PrimitiveType(){}

    @SynInit
    private void init() {
        if ("int".equals(synType)) {
            type = IntPrimitiveTypeDescriptor.INSTANCE;
        } else if ("long".equals(synType)) {
            type = LongPrimitiveTypeDescriptor.INSTANCE;
        } else if ("double".equals(synType)) {
            type = DoublePrimitiveTypeDescriptor.INSTANCE;
        } else {
            throw new IllegalStateException(synType);
        }
    }
    
    public PrimitiveTypeDescriptor getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return type + "";
    }
}
