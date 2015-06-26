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
package com.karmant.syn.sample.script.schema;

import com.karmant.syn.StringToken;
import com.karmant.syn.SynField;
import com.karmant.syn.TextPos;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Member access expression syntax node.
 */
public class MemberExpression extends TerminalExpression {
    /** The expression denoting the object to look for a member in. */
    @SynField
    private Expression synObject;
    
    /** The name of the member. */
    @SynField
    private StringToken synName;

    public MemberExpression(){}
    
    @Override
    TextPos getStartTextPos() {
        return synObject.getStartTextPos();
    }

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        Value object = synObject.evaluate(scope);
        return object.getMember(synName);
    }
    
    @Override
    public String toString() {
        return synObject + "." + synName;
    }
}
