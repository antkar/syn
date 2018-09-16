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

import java.io.IOException;

/**
 * A character stream that allows to examine a character without removing it from the stream.
 */
class LookaheadCharStream {

    private final CharStream stream;
    
    private int current;
    private final CharPos currentPos;
    private int lookahead;
    private final CharPos lookaheadPos;

    LookaheadCharStream(CharStream stream) throws IOException {
        this.stream = stream;
        
        currentPos = new CharPos();
        lookaheadPos = new CharPos();
        
        current = 0;
        lookahead = stream.read(lookaheadPos);
    }
    
    /**
     * Removes the next character from the stream and returns its code and position.
     *  
     * @param pos the position is written into that object.
     * @return the character code, or <code>-1</code>, if end-of-file reached.
     * @throws IOException if operation fails.
     */
    int next(CharPos pos) throws IOException {
        if (current != -1) {
            current = lookahead;
            currentPos.set(lookaheadPos);
            lookahead = stream.read(lookaheadPos);
        }
        
        pos.set(currentPos);
        return current;
    }
    
    /**
     * Returns the code of the next character in the stream, but does not remove the character. 
     * @return the character code, of <code>-1</code> if end-of-file reached.
     */
    int peek() {
        return lookahead;
    }
}
