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
package org.antkar.syn.internal.binder.lookup;

import org.antkar.syn.binder.SynBinderException;

/**
 * Lookup terminal expression. Used as an operand of the AND expression.
 */
abstract class LookupTermExpression {

    static final Object UNDEFINED = new Object();

    private final Class<?> clsOfValue;

    LookupTermExpression(Class<?> clsOfValue) {
        this.clsOfValue = clsOfValue;
    }

    abstract Object eval(LookupEnv env) throws SynBinderException;
    abstract String toSourceString();

    final Class<?> getClassOfValue() {
        return clsOfValue;
    }

    @Override
    public final String toString() {
        return toSourceString();
    }
}
