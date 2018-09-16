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

import org.antkar.syn.binder.SynField;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;
import org.antkar.syn.sample.script.util.MiscUtil;

/**
 * New array expression syntax node.
 */
public final class NewArrayExpression extends NewExpression {
    /** The dimensions of the array being created. */
    @SynField
    private ArrayDimension[] synDimensions;

    public NewArrayExpression(){}

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        return createArray(scope, 0);
    }

    /**
     * Creates an array for the given dimension number, including its sub-arrays.
     */
    private RValue createArray(ScriptScope scope, int dim) throws SynsException {
        if (dim == synDimensions.length) {
            return Value.forNull();
        }

        ArrayDimension dimension = synDimensions[dim];
        int len = dimension.length(scope);
        RValue[] values = new RValue[len];
        for (int i = 0; i < values.length; ++i) {
            values[i] = createArray(scope, dim + 1);
        }

        return Value.newArray(values);
    }

    @Override
    public String toString() {
        return "new " + MiscUtil.arrayToString(synDimensions, "");
    }
}
