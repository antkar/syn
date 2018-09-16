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
import org.antkar.syn.SynLexicalException;
import org.antkar.syn.TextPos;

/**
 * Multiline comments scanner.
 */
class MultipleLineCommentScanner implements IPrimitiveScanner {
    MultipleLineCommentScanner(){}
    
    @Override
    public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        if (context.current != '/' || context.lookahead() != '*') {
            //Not a comment start. Return null.
            return null;
        }

        //Skip '/*' characters.
        context.next();
        context.next();
        
        //Scan the rest.
        for (;;) {
            int k = context.current;
            if (k == '*') {
                context.next();
                if (context.current == '/') {
                    //End sequence '*/' found.
                    context.next();
                    break;
                }
            } else if (k == -1) {
                //End of file. Error.
                TextPos pos = context.getCurrentCharPos();
                throw new SynLexicalException(pos,
                        "Multiline comment is unclosed at the end of the file");
            } else {
                context.next();
            }
        }
        
        return NonePrimitiveResult.INSTANCE;
    }
}
