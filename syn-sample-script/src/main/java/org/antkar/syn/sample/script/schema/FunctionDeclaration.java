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
import java.util.List;

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;
import org.antkar.syn.binder.SynInit;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Script function declaration syntax node.
 */
public class FunctionDeclaration extends Declaration {
    /** Formal parameters. */
    @SynField
    private StringToken[] synParameters;

    /** The body. */
    @SynField
    private FunctionBody synBody;

    private FunctionObject function;

    public FunctionDeclaration(){}

    @SynInit
    private void init() {
        String scopeName = "function " + getName();
        List<StringToken> parameters = Arrays.asList(synParameters);
        function = synBody.createFunction(scopeName, parameters);
    }

    @Override
    Value evaluateValue(ScriptScope scope) throws SynsException {
        return Value.forFunction(scope, function);
    }

    @Override
    void visit(Visitor visitor) throws SynsException {
        visitor.visitFunctionDeclaration(this);
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public FunctionObject getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return "function " + getName();
    }
}
