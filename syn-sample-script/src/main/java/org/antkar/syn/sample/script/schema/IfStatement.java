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
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Script <code>if</code> statement syntax node.
 */
public final class IfStatement extends Statement {
    /** The text position of the first token. */
    @SynField
    private TextPos synPos;

    /** The condition expression. */
    @SynField
    private Expression synExpression;

    /** The statement which has to be executed if the condition is <code>true</code>. */
    @SynField
    private Statement synTrueStatement;

    /** The statement which has to be executed if the condition is <code>false</code>.
     * Can be <code>null</code>. */
    @SynField
    private Statement synFalseStatement;

    public IfStatement(){}

    @Override
    TextPos getStartTextPos() {
        return synPos;
    }

    @Override
    StatementResult execute0(ScriptScope scope) throws SynsException {
        Value value = synExpression.evaluate(scope);
        if (value.toOperand().booleanValue()) {
            return synTrueStatement.execute(scope);
        } else if (synFalseStatement != null) {
            return synFalseStatement.execute(scope);
        } else {
            return StatementResult.NONE;
        }
    }

    @Override
    public String toString() {
        return "if (...)";
    }
}
