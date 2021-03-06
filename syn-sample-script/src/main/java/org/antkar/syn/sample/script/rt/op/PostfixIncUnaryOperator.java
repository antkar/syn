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
package org.antkar.syn.sample.script.rt.op;

import org.antkar.syn.sample.script.rt.value.LValue;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Script Language postfix <code>++</code> operator.
 */
final class PostfixIncUnaryOperator extends AssignmentUnaryOperator {
    PostfixIncUnaryOperator() {
        super("++");
    }

    @Override
    long evaluate(long value) {
        return value + 1;
    }

    @Override
    double evaluate(double value) {
        return value + 1;
    }

    @Override
    Value resultValue(RValue prevValue, LValue value) {
        return prevValue;
    }
}
