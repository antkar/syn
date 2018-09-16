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
import java.io.Reader;

import org.antkar.syn.internal.Checks;

/**
 * Character stream. Reads input characters from a {@link Reader}, tracks character position (line and column
 * numbers).
 */
final class CharStream {
    private final Reader reader;

    private boolean eof;
    private int line;
    private int column;
    private int offset;

    CharStream(Reader reader) {
        this.reader = Checks.notNull(reader);
    }

    /**
     * Reads the next character from the input. Returns the character itself and its position.
     *
     * @param pos the character position object where to write the position to.
     * @return the code of the character, or <code>-1</code> in case of end-of-file.
     * @throws IOException
     */
    int read(CharPos pos) throws IOException {
        //Set the position before reading the character.
        pos.set(line, column, offset);

        if (eof) {
            return -1;
        }

        //Read the character from the input.
        int k = reader.read();
        if (k == -1) {
            return -1;
        }

        //Calculate the position of the character following the one having been read.
        if (k == '\n') {
            column = 0;
            ++line;
        } else {
            ++column;
        }
        ++offset;

        return k;
    }
}
