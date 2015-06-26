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
package org.antkar.syn;

import org.antkar.syn.BoundObject;
import org.antkar.syn.SynBinderException;

/**
 * Lookup "owner" expression.
 */
class LookupOwnerExpression extends LookupComplexTermExpression {

    LookupOwnerExpression(Class<?> clsOfValue, LookupTermExpression baseExpression) {
        super(clsOfValue, baseExpression);
    }

    @Override
    Object eval(LookupEnv env) throws SynBinderException {
        LookupTermExpression baseExpression = getBaseExpression();
        Object eval = baseExpression.eval(env);
        
        Object result;
        if (eval == null || eval == UNDEFINED) {
            result = UNDEFINED;
        } else {
            BoundObject bEval = (BoundObject) eval; 
            result = bEval.getOwner();
        }
        
        return result;
    }

    @Override
    String toSourceString() {
        LookupTermExpression baseExpression = getBaseExpression();
        String baseSourceString = baseExpression.toSourceString();
        String string = baseSourceString + ".owner";
        return string;
    }

}
