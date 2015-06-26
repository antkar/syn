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

import org.antkar.syn.CharPos;
import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.TerminalNode;
import org.antkar.syn.TextPos;

/**
 * Text position buffer - a mutable version of {@link TextPos}. Used to pass a text position to
 * {@link TerminalNode}'s constructor without creating an instance of an object (in order to avoid
 * unnecessary overhead).
 */
class PosBuffer {

    private SourceDescriptor source;
    private int offset;
    private int line;
    private int column;
    private int length;

    PosBuffer(){}
    
    void set(CharPos pos, SourceDescriptor sourceDescriptor, int endOffset) {
        source = sourceDescriptor;
        offset = pos.offset;
        line = pos.line + 1;
        column = pos.column + 1;
        length = endOffset - offset;
    }

    SourceDescriptor getSource() {
        return source;
    }

    int getOffset() {
        return offset;
    }

    int getLine() {
        return line;
    }

    int getColumn() {
        return column;
    }

    int getLength() {
        return length;
    }
}
