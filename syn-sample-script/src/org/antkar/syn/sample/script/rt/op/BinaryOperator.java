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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.rt.op.operand.OperandType;
import org.antkar.syn.sample.script.rt.value.RValue;

/**
 * Implements a binary Script Language expression operator.
 */
public abstract class BinaryOperator extends Operator {
    /** Maps arithmetical binary operator literals to corresponding {@link BinaryOperator}
     * instances. */
    private static final Map<String, BinaryOperator> OPERATORS;
    
    static {
        Map<String, BinaryOperator> map = new HashMap<>();
        map.put("||", new OrBinaryOperator());
        map.put("&&", new AndBinaryOperator());
        map.put("==", new EqBinaryOperator());
        map.put("!=", new NeBinaryOperator());
        map.put("<", new LtBinaryOperator());
        map.put(">", new GtBinaryOperator());
        map.put("<=", new LeBinaryOperator());
        map.put(">=", new GeBinaryOperator());
        map.put("+", new AddBinaryOperator());
        map.put("-", new SubBinaryOperator());
        map.put("*", new MulBinaryOperator());
        map.put("/", new DivBinaryOperator());
        map.put("%", new ModBinaryOperator());
        OPERATORS = Collections.unmodifiableMap(map);
    }
    
    /** Maps compound assignment operators literals to corresponding arithmetical operators. */
    private static final Map<String, BinaryOperator> ASSIGNMENT_OPERATORS;
    
    static {
        Map<String, BinaryOperator> map = new HashMap<>();
        map.put("+=", new AddBinaryOperator());
        map.put("-=", new SubBinaryOperator());
        map.put("*=", new MulBinaryOperator());
        map.put("/=", new DivBinaryOperator());
        map.put("%=", new ModBinaryOperator());
        map.put("&=", new AndBinaryOperator());
        map.put("|=", new OrBinaryOperator());
        ASSIGNMENT_OPERATORS = map;
    }
    
    BinaryOperator(String opLiteral) {
        super(opLiteral);
    }

    /**
     * Returns a binary operator for the specified literal.
     */
    public static BinaryOperator forLiteral(String literal) {
        BinaryOperator op = OPERATORS.get(literal);
        assert op != null;
        return op;
    }

    /**
     * Returns a binary operator for the specified compound assignment literal.
     */
    public static BinaryOperator forAssignmentLiteral(String literal) {
        BinaryOperator op = ASSIGNMENT_OPERATORS.get(literal);
        assert op != null;
        return op;
    }

    /**
     * Tries to evaluate the result of the operator using only the left operand. Overridden by
     * AND and OR operators to provide a short-circuit evaluation. Returns <code>null</code> if
     * a short-circuit evaluation is not possible.
     * 
     * @throws SynsException on error. 
     */
    public RValue evaluate(Operand left) throws SynsException {
        return null;
    }
    
    /**
     * Evaluates the result of the operator for given operands.
     */
    public RValue evaluate(Operand left, Operand right) throws SynsException {
        OperandType leftType = left.getType();
        OperandType rightType = right.getType();
        
        //Numeric type promotion.
        if (leftType != rightType) {
            //If one of the operands is a floating-point while the other one is an integer, convert
            //the latter to floating-point.
            if (leftType == OperandType.DOUBLE && rightType == OperandType.LONG) {
                //Use DOUBLE.
            } else if (leftType == OperandType.LONG && rightType == OperandType.DOUBLE) {
                leftType = OperandType.DOUBLE;
            } else {
                throw errOperandTypeMissmatch(leftType, rightType);
            }
        }
        
        //At this point both operands considered to be of the same type.
        //Evaluate the result depending on that type.
        RValue result;
        if (leftType == OperandType.BOOLEAN) {
            result = evaluate(left.booleanValue(), right.booleanValue());
        } else if (leftType == OperandType.LONG) {
            result = evaluate(left.longValue(), right.longValue());
        } else if (leftType == OperandType.DOUBLE) {
            result = evaluate(left.doubleValue(), right.doubleValue());
        } else {
            throw errOperandTypeMissmatch(leftType);
        }
        
        return result;
    }
    
    /**
     * Evaluates the operator for boolean operands.
     */
    RValue evaluate(boolean left, boolean right) throws SynsException {
        throw errOperandTypeMissmatch(OperandType.BOOLEAN);
    }
    
    /**
     * Evaluates the operator for integer operands.
     */
    RValue evaluate(long left, long right) throws SynsException {
        throw errOperandTypeMissmatch(OperandType.LONG);
    }
    
    /**
     * Evaluates the operator for floating-point operands.
     */
    RValue evaluate(double left, double right) throws SynsException {
        throw errOperandTypeMissmatch(OperandType.DOUBLE);
    }
}
