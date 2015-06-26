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
package com.karmant.syn.sample.script.rt.op;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Script Language unary operator.
 */
public abstract class UnaryOperator extends Operator {
    
    /** Maps prefix unary operator literals to corresponding {@link UnaryOperator} instances. */
    private static final Map<String, UnaryOperator> PREFIX_OPERATORS;
    
    static {
        Map<String, UnaryOperator> map = new HashMap<>();
        map.put("++", new PrefixIncUnaryOperator());
        map.put("--", new PrefixDecUnaryOperator());
        map.put("+", new PlusUnaryOperator());
        map.put("-", new MinusUnaryOperator());
        map.put("!", new LogicalNotUnaryOperator());
        PREFIX_OPERATORS = Collections.unmodifiableMap(map);
    }
    
    /** Maps postfix unary operator literals to corresponding {@link UnaryOperator} instances. */
    private static final Map<String, UnaryOperator> POSTFIX_OPERATORS;
    
    static {
        Map<String, UnaryOperator> map = new HashMap<>();
        map.put("++", new PostfixIncUnaryOperator());
        map.put("--", new PostfixDecUnaryOperator());
        POSTFIX_OPERATORS = Collections.unmodifiableMap(map);
    }
    
    UnaryOperator(String opLiteral) {
        super(opLiteral);
    }
    
    /**
     * Returns a prefix unary operator for the specified literal.
     */
    public static UnaryOperator forPrefixLiteral(String literal) {
        UnaryOperator op = PREFIX_OPERATORS.get(literal);
        assert op != null;
        return op;
    }
    
    /**
     * Returns a postfix unary operator for the specified literal.
     */
    public static UnaryOperator forPostfixLiteral(String literal) {
        UnaryOperator op = POSTFIX_OPERATORS.get(literal);
        assert op != null;
        return op;
    }
    
    /**
     * Evaluates the operator for the given operand value.
     */
    public abstract Value evaluate(Value value) throws SynsException;
}
