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

import org.antkar.syn.SynField;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.LValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Script variable declaration syntax node.
 */
public class VariableDeclaration extends Declaration {
    /** Initialization expression. Can be <code>null</code>. */
    @SynField
    private Expression synExpression;

    public VariableDeclaration(){}
    
    @Override
    Value evaluateValue(ScriptScope scope) throws SynsException {
        Value expressionValue = synExpression == null ? Value.forNull()
                : synExpression.evaluate(scope);

        LValue variable = Value.newVariable(expressionValue);
        return variable;
    }
    
    @Override
    void visit(Visitor visitor) throws SynsException {
        visitor.visitVariableDeclaration(this);
    }
    
    @Override
    public String toString() {
        return "var " + getName() + (synExpression == null ? "" : " = " + synExpression);
    }
}
