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
import org.antkar.syn.binder.LongToken;
import org.antkar.syn.binder.SynField;
import org.antkar.syn.binder.SynInit;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Integer literal expression syntax node.
 */
public final class IntegerLiteralExpression extends LiteralExpression {
    /** The value of the literal. */
    @SynField
    private LongToken synValue;

    /** The script representation of the value. */
    private Value value;

    public IntegerLiteralExpression(){}

    @SynInit
    private void init() {
        value = Value.forLong(synValue.getValue());
    }

    @Override
    TextPos getStartTextPos() {
        return synValue.getPos();
    }

    @Override
    Value evaluate0(ScriptScope scope) {
        return value;
    }

    @Override
    public String toString() {
        return value + "";
    }
}
