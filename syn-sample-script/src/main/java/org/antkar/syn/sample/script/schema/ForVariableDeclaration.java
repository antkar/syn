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
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Script <code>for</code> statement variable declaration syntax node.
 */
public final class ForVariableDeclaration {
    /** The name of the variable. */
    @SynField
    private StringToken synName;

    /** The value of the variable, if any. */
    @SynField
    private Expression synExpression;

    public ForVariableDeclaration(){}

    /**
     * Adds this variable to the specified scope.
     */
    void addToScope(ScriptScope scope) throws SynsException {
        Value value = synExpression.evaluate(scope);
        Value variable = Value.newVariable(value);
        scope.addValue(synName, variable);
    }

    @Override
    public String toString() {
        return "var " + synName + " = " + synExpression;
    }
}
