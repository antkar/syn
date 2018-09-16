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

import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.TextPos;

/**
 * Character position. A mutable object containing a line and a column number and an offset from the beginning
 * of the input.
 */
final class CharPos {
    private int line;
    private int column;
    private int offset;

    CharPos() {
    }

    int line() {
        return line;
    }

    int column() {
        return column;
    }

    int offset() {
        return offset;
    }

    void set(int aLine, int aColumn, int aOffset) {
        line = aLine;
        column = aColumn;
        offset = aOffset;
    }

    void set(CharPos pos) {
        line = pos.line;
        column = pos.column;
        offset = pos.offset;
    }

    TextPos toTextPos(SourceDescriptor sourceDescriptor, int endOffset) {
        return new TextPos(sourceDescriptor, offset, line + 1, column + 1, endOffset - offset);
    }
}
