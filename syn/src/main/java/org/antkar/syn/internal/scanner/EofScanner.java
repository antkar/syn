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
package org.antkar.syn.internal.scanner;

import org.antkar.syn.TokenDescriptor;

/**
 * End-of-file token scanner.
 */
final class EofScanner implements IPrimitiveScanner {
    private static final IPrimitiveResult PRIMITIVE_RESULT =
            new TokenNodePrimitiveResult(TokenDescriptor.END_OF_FILE);

    EofScanner(){}

    @Override
    public IPrimitiveResult scan(PrimitiveContext context) {
        if (context.current == -1) {
            //End-of-file. Return corresponding token.
            return PRIMITIVE_RESULT;
        }
        return null;
    }
}
