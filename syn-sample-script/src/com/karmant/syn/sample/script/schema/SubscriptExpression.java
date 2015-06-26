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

import com.karmant.syn.SynField;
import com.karmant.syn.TextPos;
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.op.operand.Operand;
import com.karmant.syn.sample.script.rt.value.RValue;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Array access expression syntax node.
 */
public class SubscriptExpression extends TerminalExpression {
    /** The expression to be used as an array. */
    @SynField
    private Expression synArray;
    
    /** The index expression. */
    @SynField
    private Expression synIndex;

    public SubscriptExpression(){}
    
    @Override
    TextPos getStartTextPos() {
        return synArray.getStartTextPos();
    }

    @Override
    Value evaluate0(ScriptScope scope) throws SynsException {
        Value array = synArray.evaluate(scope);
        RValue rarray = array.toRValue();
        Value index = synIndex.evaluate(scope);
        Operand indexOperand = index.toOperand();
        int i = indexOperand.intValue();
        return rarray.getArrayElement(i);
    }
    
    @Override
    public String toString() {
        return String.format("(%s[%s])", synArray, synIndex);
    }
}
