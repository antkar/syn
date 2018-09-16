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

import java.util.Collections;
import java.util.List;

import org.antkar.syn.binder.SynBinderException;

/**
 * Lookup AND expression. Applies the logical AND operation to the set of sub-expressions.
 */
final class LookupAndExpression extends LookupExpression {
    private final List<LookupRelExpression> relExpressions;

    LookupAndExpression(List<LookupRelExpression> relExpressions) {
        this.relExpressions = Collections.unmodifiableList(relExpressions);
    }

    @Override
    boolean eval(LookupEnv env) throws SynBinderException {
        for (LookupRelExpression relExpression : relExpressions) {
            boolean eval = relExpression.eval(env);
            if (!eval) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (relExpressions.size() == 1) {
            return relExpressions.get(0) + "";
        }

        StringBuilder bld = new StringBuilder();
        String sep = "";
        for (LookupRelExpression expr : relExpressions) {
            bld.append(sep);
            bld.append(expr);
            sep = " && ";
        }

        return bld.toString();
    }
}
