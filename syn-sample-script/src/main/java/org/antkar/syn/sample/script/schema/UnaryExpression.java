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

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;
import org.antkar.syn.binder.SynInit;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.UnaryOperator;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Unary expression syntax node.
 */
public abstract class UnaryExpression extends Expression {
    /** The operand. */
    @SynField
    private Expression synExpression;

    /** The operator literal. */
    @SynField
    private StringToken synOp;

    /** Corresponding operator instance. */
    private UnaryOperator op;

    public UnaryExpression(){}

    @SynInit
    private void init() {
        op = getOperator(synOp.getValue());
    }

    /**
     * Returns the literal of the operator.
     */
    StringToken getOp() {
        return synOp;
    }

    /**
     * Returns the expression.
     */
    Expression getExpression() {
        return synExpression;
    }

    /**
     * Returns the operator for the specified literal.
     */
    abstract UnaryOperator getOperator(String literal);

    @Override
    final Value evaluate0(ScriptScope scope) throws SynsException {
        Value value = synExpression.evaluate(scope);
        return op.evaluate(value);
    }
}
