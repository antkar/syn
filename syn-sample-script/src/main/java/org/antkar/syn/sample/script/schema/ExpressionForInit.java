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

import java.util.Arrays;

import org.antkar.syn.binder.SynField;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;

/**
 * Syntax node for an arbitrary expression initializer for a <code>for</code> statement.
 */
public class ExpressionForInit extends ForInit {
    /** The list of expressions. */
    @SynField
    private Expression[] synExpressions;

    public ExpressionForInit(){}

    @Override
    void execute(ScriptScope scope) throws SynsException {
        for (Expression expression : synExpressions) {
            expression.evaluate(scope);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(synExpressions);
    }
}
