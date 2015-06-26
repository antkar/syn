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
import com.karmant.syn.sample.script.rt.ScriptScope;
import com.karmant.syn.sample.script.rt.StatementResult;
import com.karmant.syn.sample.script.rt.SynsException;
import com.karmant.syn.sample.script.rt.op.operand.Operand;
import com.karmant.syn.sample.script.rt.value.Value;

/**
 * Regular <code>for</code> statement syntax node.
 */
public class RegularForStatement extends ForStatement {
    /** The initializer. */
    @SynField
    private ForInit synInit;
    
    /** The condition expression. */
    @SynField
    private Expression synExpression;
    
    /** Update expressions. */
    @SynField
    private Expression[] synUpdate;
    
    /** The statement to be repeated. */
    @SynField
    private Statement synStatement;

    public RegularForStatement(){}

    @Override
    StatementResult execute0(ScriptScope scope) throws SynsException {
        //Create a scope and put the control variable there, if any.
        scope = scope.deriveLoopScope("for");
        if (synInit != null) {
            synInit.execute(scope);
        }
        
        //Execute the loop.
        while (checkCondition(scope)) {
            //Execute the body.
            StatementResult result = synStatement.execute(scope);
            if (result.isBreak()) {
                break;
            } else if (result.isReturn()) {
                return result;
            }
            
            //Execute update expressions.
            for (Expression expression : synUpdate) {
                expression.evaluate(scope);
            }
        }
        
        return StatementResult.NONE;
    }
    
    /**
     * Checks whether the condition expression is satisfied.
     */
    private boolean checkCondition(ScriptScope scope) throws SynsException {
        if (synExpression == null) {
            //No condition - assume true.
            return true;
        }
        
        //Evaluate the condition expression.
        Value value = synExpression.evaluate(scope);
        Operand operand = value.toOperand();
        return operand.booleanValue();
    }
}
