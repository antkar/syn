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
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.StatementResult;
import org.antkar.syn.sample.script.rt.SynsException;

/**
 * Expression statement syntax node.
 */
public final class ExpressionStatement extends Statement {
    /** The expression. */
    @SynField
    private Expression synExpression;

    public ExpressionStatement(){}

    @Override
    TextPos getStartTextPos() {
        return synExpression.getStartTextPos();
    }

    @Override
    StatementResult execute0(ScriptScope scope) throws SynsException {
        synExpression.evaluate(scope);
        return StatementResult.NONE;
    }

    @Override
    public String toString() {
        return synExpression + "";
    }
}
