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
package com.karmant.syn;

/**
 * Lookup reference expression. Returns a referenced bound object by a reference name.
 */
class LookupReferenceExpression extends LookupComplexTermExpression {

    private final String fieldName;
    
    LookupReferenceExpression(
            Class<?> clsOfValue,
            LookupTermExpression baseExpression,
            String fieldName)
    {
        super(clsOfValue, baseExpression);
        this.fieldName = fieldName;
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
            result = bEval.getReferencedObject(fieldName);
        }
        
        return result;
    }

    @Override
    String toSourceString() {
        LookupTermExpression baseExpression = getBaseExpression();
        String baseSourceString = baseExpression.toSourceString();
        String string = baseSourceString + "." + fieldName;
        return string;
    }
}
