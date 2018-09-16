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

import org.antkar.syn.SynException;

/**
 * Single-line comment scanner. Scans comments starting with '//'.
 */
final class SingleLineCommentScanner implements IPrimitiveScanner {
    SingleLineCommentScanner(){}

    @Override
    public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        if (context.current != '/' || context.lookahead() != '/') {
            //Not '//'. Return null.
            return null;
        }

        //Skip all characters until the end of the line or end of file.
        while (context.current != -1 && context.current != '\n') {
            context.next();
        }

        return NonePrimitiveResult.INSTANCE;
    }
}
