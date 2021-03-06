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

import org.antkar.syn.TextPos;
import org.antkar.syn.binder.SynField;
import org.antkar.syn.sample.script.rt.PrimitiveTypeDescriptor;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Explicit type cast expression syntax node.
 */
public final class ExplicitCastExpression extends Expression {
    /** The position of the first token. */
    @SynField
    private TextPos synPos;

    /** The type to be casted to. */
    @SynField
    private PrimitiveType synType;

    /** The value to be casted. */
    @SynField
    private Expression synExpression;

    public ExplicitCastExpression(){}

    @Override
    TextPos getStartTextPos() {
        return synPos;
    }

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        PrimitiveTypeDescriptor type = synType.getType();

        Value value = synExpression.evaluate(scope);
        Operand operand = value.toOperand();
        RValue result = type.cast(operand);
        return result;
    }

    @Override
    public String toString() {
        return String.format("((%s) %s)", synType, synExpression);
    }
}
