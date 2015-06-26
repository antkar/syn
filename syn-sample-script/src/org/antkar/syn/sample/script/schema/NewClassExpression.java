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

import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.RValue;
import org.antkar.syn.sample.script.rt.value.Value;
import org.antkar.syn.sample.script.util.MiscUtil;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;

/**
 * New object instantiation expression syntax node.
 */
public class NewClassExpression extends NewExpression {
    /** The name of the type of the object. */
    @SynField
    private StringToken[] synTypeName;
    
    /** Constructor actual arguments. */
    @SynField
    private Expression[] synArguments;

    public NewClassExpression(){}

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        Value value = scope.getValue(synTypeName);
        
        RValue[] arguments = new RValue[synArguments.length];
        for (int i = 0; i < arguments.length; ++i) {
            Value argument = synArguments[i].evaluate(scope);
            arguments[i] = argument.toRValue();
        }
        
        return value.newObject(arguments);
    }
    
    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("new ");
        MiscUtil.appendArray(bld, synTypeName, ".");
        bld.append("( ");
        MiscUtil.appendArray(bld, synArguments, ", ");
        bld.append(" )");
        return bld + "";
    }
}
