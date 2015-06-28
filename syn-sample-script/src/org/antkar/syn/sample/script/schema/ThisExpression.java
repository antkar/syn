/*
 * Copyright 2015 Anton Karmanov
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
import org.antkar.syn.TextPos;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.TextSynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Expression "this".
 */
public class ThisExpression extends TerminalExpression {
    @SynField
    private TextPos synPos;
    
    public ThisExpression(){}

    @Override
    TextPos getStartTextPos() {
        return synPos;
    }

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        Value value = scope.getThisValueOpt();
        if (value == null) {
            throw new TextSynsException("No 'this' in scope", synPos);
        }
        return value;
    }
}
