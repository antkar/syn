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
import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Declaration syntax node.
 */
public abstract class Declaration {
    /** The text position of the first token. */
    @SynField
    private TextPos synPos;

    /** The name of the declaration. */
    @SynField
    private StringToken synName;

    public Declaration(){}

    /**
     * Returns the text position of the first token of this declaration.
     */
    TextPos getStartTextPos() {
        return synPos;
    }

    /**
     * Returns the name of the declaration.
     */
    public final String getName() {
        return synName.getValue();
    }

    /**
     * Returns the name token of the declaration.
     */
    public final StringToken getNameTk() {
        return synName;
    }

    /**
     * Adds this declaration to the specified scope.
     */
    public final Value addToScope(ScriptScope scope) throws SynsException {
        Value value = evaluateValue(scope);
        scope.addValue(synName, value);
        return value;
    }

    /**
     * Evaluates the initial value of the declaration.
     */
    abstract Value evaluateValue(ScriptScope scope) throws SynsException;

    /**
     * Returns <code>true</code> if this declaration is a function declaration.
     */
    public boolean isFunction() {
        return false;
    }

    abstract void visit(Visitor visitor) throws SynsException;

    interface Visitor {
        void visitConstantDeclaration(ConstantDeclaration declaration) throws SynsException;
        void visitVariableDeclaration(VariableDeclaration declaration) throws SynsException;
        void visitFunctionDeclaration(FunctionDeclaration declaration) throws SynsException;
        void visitClassDeclaration(ClassDeclaration declaration) throws SynsException;
    }
}
