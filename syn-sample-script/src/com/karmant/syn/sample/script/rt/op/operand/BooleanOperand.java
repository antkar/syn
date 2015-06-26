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
package com.karmant.syn.sample.script.rt.op.operand;

import com.karmant.syn.sample.script.rt.SynsException;

/**
 * Operand of type <code>boolean</code>.
 */
class BooleanOperand extends Operand {
    static final BooleanOperand TRUE = new BooleanOperand(true);
    static final BooleanOperand FALSE = new BooleanOperand(false);
    
    private final boolean value;

    private BooleanOperand(boolean value) {
        this.value = value;
    }
    
    static Operand valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public OperandType getType() {
        return OperandType.BOOLEAN;
    }

    @Override
    public boolean booleanValue() throws SynsException {
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
}
