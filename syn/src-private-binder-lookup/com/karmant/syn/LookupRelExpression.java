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
 * Lookup relation expression. Returns a boolean result.
 */
abstract class LookupRelExpression extends LookupExpression {

    private final LookupEqualityChecker equalityChecker;
    private final LookupTermExpression left;
    private final LookupTermExpression right;
    
    LookupRelExpression(
            LookupEqualityChecker equalityChecker,
            LookupTermExpression left,
            LookupTermExpression right)
    {
        this.equalityChecker = equalityChecker;
        this.left = left;
        this.right = right;
    }
    
    LookupTermExpression getLeft() {
        return left;
    }
    
    LookupTermExpression getRight() {
        return right;
    }

    abstract boolean calcResult(boolean equal);

    @Override
    boolean eval(LookupEnv env) throws SynBinderException {
        Object leftObj = left.eval(env);
        Object rightObj = right.eval(env);
        
        boolean result;
        if (leftObj == LookupTermExpression.UNDEFINED || rightObj == LookupTermExpression.UNDEFINED) {
            result = false;
        } else {
            boolean equal = equalityChecker.equal(leftObj, rightObj);
            result = calcResult(equal);
        }
        
        return result;
    }
}
