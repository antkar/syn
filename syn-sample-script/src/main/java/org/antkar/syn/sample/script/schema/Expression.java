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
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.TextSynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * An expression syntax node.
 */
public abstract class Expression {
    public Expression(){}

    /**
     * Returns the text position of the first element of this expression. Used for error reporting.
     */
    abstract TextPos getStartTextPos();

    /**
     * Evaluates the expression in the specified scope. Takes care about exceptions, adding the text
     * position.
     */
    final Value evaluate(ScriptScope scope) throws SynsException {
        try {
            return evaluate0(scope);
        } catch (TextSynsException e) {
            throw e;
        } catch (SynsException e) {
            throw new TextSynsException(e, getStartTextPos());
        } catch (Throwable e) {
            throw new TextSynsException(e, getStartTextPos());
        }
    }

    /**
     * Evaluates the expression in the specified scope. Must not be called directly.
     */
    abstract Value evaluate0(ScriptScope scope) throws SynsException;

    FunctionObject.Factory getFunctionObjectFactory() {
        return new ExpressionFunctionObject.Factory(this);
    }
}
