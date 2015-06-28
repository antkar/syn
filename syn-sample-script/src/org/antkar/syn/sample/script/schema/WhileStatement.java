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
import org.antkar.syn.sample.script.rt.StatementResult;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.op.operand.Operand;
import org.antkar.syn.sample.script.rt.value.Value;

import org.antkar.syn.SynField;
import org.antkar.syn.TextPos;

/**
 * Script <code>while</code> statement syntax node.
 */
public class WhileStatement extends Statement {
    /** The text position of the first token. */
    @SynField
    private TextPos synPos;

    /** The condition expression. */
    @SynField
    private Expression synExpression;
    
    /** The statement to repeat. */
    @SynField
    private Statement synStatement;

    public WhileStatement(){}

    @Override
    TextPos getStartTextPos() {
        return synPos;
    }

    @Override
    StatementResult execute0(ScriptScope scope) throws SynsException {
        //Create a nested scope to execute the loop in it.
        scope = scope.nestedLoopScope("while");
        
        for (;;) {
            //Check the condition.
            Value value = synExpression.evaluate(scope);
            Operand operand = value.toOperand();
            if (!operand.booleanValue()) {
                break;
            }
            
            //Execute the statement.
            StatementResult result = synStatement.execute(scope);
            if (result.isReturn()) {
                return result;
            } else if (result.isBreak()) {
                break;
            }
        }
        
        return StatementResult.NONE;
    }
    
    @Override
    public String toString() {
        return "while (...)";
    }
}
