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
import org.antkar.syn.sample.script.rt.op.operand.OperandType;

/**
 * Implements a particular Script Language expression operator.
 */
public abstract class Operator {
    /** The literal of the operator. */
    private final String opLiteral;
    
    Operator(String opLiteral) {
        this.opLiteral = opLiteral;
    }
    
    /**
     * Creates and throws an operand type mismatch exception for a single operand.
     */
    SynsException errOperandTypeMissmatch(OperandType type) throws SynsException {
        throw SynsException.format(
                "Operator '%s' is undefined for type %s",
                opLiteral,
                type.getDescriptiveName());
    }
    
    /**
     * Creates and throws an operand type mismatch exception for two operands.
     */
    SynsException errOperandTypeMissmatch(OperandType leftType, OperandType rightType)
            throws SynsException
    {
        throw SynsException.format(
                "Operator '%s' is undefined for types %s, %s", opLiteral,
                leftType.getDescriptiveName(),
                rightType.getDescriptiveName());
    }
    
    @Override
    public String toString() {
        return opLiteral;
    }
}
