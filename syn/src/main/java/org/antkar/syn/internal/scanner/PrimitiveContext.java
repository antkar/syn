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

import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.SynException;
import org.antkar.syn.SynLexicalException;
import org.antkar.syn.SynTextException;
import org.antkar.syn.TextPos;
import org.antkar.syn.internal.Checks;
import org.antkar.syn.internal.PosBuffer;

/**
 * A context of a primitive scanner execution. Contains the input stream, the current character,
 * a helper string buffer and other useful resources.
 */
final class PrimitiveContext {
    private final SourceDescriptor sourceDescriptor;
    private final LookaheadCharStream charStream;

    private final CharPos startPos;
    private final CharPos currentPos;
    private final PosBuffer posBuffer;

    private final StringBuilder stringBuilder;

    private int maxBufferLength;

    /**
     * The code of the current character, or <code>-1</code> if end of the input is reached.
     */
    int current;

    PrimitiveContext(SourceDescriptor sourceDescriptor, LookaheadCharStream charStream) throws SynException {
        this.sourceDescriptor = Checks.notNull(sourceDescriptor);
        this.charStream = Checks.notNull(charStream);

        startPos = new CharPos();
        currentPos = new CharPos();
        posBuffer = new PosBuffer();

        stringBuilder = new StringBuilder();
        maxBufferLength = 0;

        next();
    }

    /**
     * Invoked at the beginning of a token scanning. Remembers the current position as the start position of a
     * token and clears the helper string buffer.
     */
    void startToken() {
        startPos.set(currentPos);
        stringBuilder.setLength(0);
        maxBufferLength = 0;
    }

    /**
     * Reads the next character from the input stream, so that that character becomes the current one.
     */
    void next() throws SynException {
        try {
            current = charStream.next(currentPos);
        } catch (IOException e) {
            throw new SynTextException(getCurrentCharPos(), "I/O error: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the next input character without removing it from the stream.
     */
    int lookahead() {
        int k = charStream.peek();
        return k;
    }

    /**
     * Sets the helper string buffer size limit.
     */
    void setMaxBufferLength(int maxBufferLength) {
        this.maxBufferLength = maxBufferLength;
    }

    /**
     * Appends the current character to the helper string buffer.
     */
    void append() throws SynLexicalException {
        append((char)current);
    }

    /**
     * Appends the specified character to the helper string buffer.
     * @throws SynLexicalException if buffer size limit is reached.
     */
    void append(char ch) throws SynLexicalException {
        if (stringBuilder.length() >= maxBufferLength) {
            TextPos pos = getCurrentCharPos();
            throw new SynLexicalException(pos, "Literal is too long");
        }
        stringBuilder.append(ch);
    }

    /**
     * Returns the string currently contained in the helper string buffer.
     */
    String getString() {
        return stringBuilder.toString();
    }

    /**
     * Returns the helper string buffer.
     */
    StringBuilder getStringBuilder() {
        return stringBuilder;
    }

    /**
     * Returns the start position of the current token in form of a {@link PosBuffer}. The same instance of
     * the class is always returned.
     */
    PosBuffer getStartPosBuffer() {
        int offset = startPos.offset();
        int line = startPos.line() + 1;
        int column = startPos.column() + 1;
        int length = currentPos.offset() - offset;
        posBuffer.set(sourceDescriptor, offset, line, column, length);
        return posBuffer;
    }

    /**
     * Returns the start position of the current token in form of a {@link TextPos}. A new instance is always
     * created.
     */
    TextPos getStartPos() {
        return startPos.toTextPos(sourceDescriptor, currentPos.offset());
    }

    /**
     * Returns the offset of the start position of the current token.
     */
    int getStartOffset() {
        return startPos.offset();
    }

    /**
     * Returns the offset of the end position of the current token.
     */
    int getEndOffset() {
        return currentPos.offset();
    }

    /**
     * Returns the offset of the current character's position.
     */
    int getCurrentOffset() {
        return currentPos.offset();
    }

    /**
     * Returns the current character's position. A new instance of {@link TextPos} is created every time.
     */
    TextPos getCurrentCharPos() {
        TextPos pos = currentPos.toTextPos(sourceDescriptor, currentPos.offset());
        return pos;
    }

    /**
     * Returns the position of the current token. Differs from {@link #getStartPos()} by returning a correct
     * token length.
     */
    TextPos getCurrentTokenPos() {
        TextPos pos = startPos.toTextPos(sourceDescriptor, currentPos.offset());
        return pos;
    }

    @Override
    public String toString() {
        return sourceDescriptor + "(" + (currentPos.line() + 1) + ":" + (currentPos.column() + 1) + ")";
    }
}
