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

import org.antkar.syn.internal.Checks;

/**
 * A position in an input text. Possible modes are:
 * <ol>
 * <li>Normal. Contains an input descriptor, an offset, line, column and the length of a segment.
 * Abstract Syntax Tree generated by the parser contains only this kind of input positions.</li>
 * <li>Point. Same as Normal, except the length is <code>0</code>. Describes a point in the input. Used
 * for errors signaling.</li>
 * <li>Resource. Contains only an input descriptor. Used for errors signaling when an error is not
 * associated with a particular position in a text.</li>
 * <li>Undefined, or Null. Never returned by the library, but may be useful for a client code.</li>
 * </ol>
 */
public final class TextPos {

    /**
     * Null, or undefined, position.
     */
    public static final TextPos NULL = new TextPos();

    private final SourceDescriptor source;
    private final int offset;
    private final int line;
    private final int column;
    private final int length;

    /**
     * Constructs a null position.
     */
    private TextPos() {
        source = null;
        offset = -1;
        line = -1;
        column = -1;
        length = 0;
    }

    /**
     * Constructs a position describing an entire input, without a specific position in it. May be useful
     * for some errors reporting.
     *
     * @param source the descriptor of the input. Cannot be <code>null</code>.
     */
    public TextPos(SourceDescriptor source) {
        this.source = Checks.notNull(source);
        offset = -1;
        line = -1;
        column = -1;
        length = 0;
    }

    /**
     * Constructs a position with all the parameters known.
     *
     * @param source the source descriptor. Cannot be <code>null</code>.
     * @param offset the input offset.
     * @param line the line number.
     * @param column the column number.
     * @param length the segment length.
     */
    public TextPos(SourceDescriptor source, int offset, int line, int column, int length) {
        Checks.argument(offset >= 0);
        Checks.argument(line >= 1);
        Checks.argument(column >= 1);
        Checks.argument(length >= 0);

        this.offset = offset;
        this.source = Checks.notNull(source);
        this.line = line;
        this.column = column;
        this.length = length;
    }

    /**
     * Returns the source descriptor describing the resource where this position points to.
     *
     * @return the source descriptor, or <code>null</code> for an undefined position.
     */
    public SourceDescriptor getSource() {
        return source;
    }

    /**
     * Returns the offset of the position in the input, starting with <code>0</code>.
     *
     * @return the offset, or <code>-1</code> if the offset is undefined.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the line number, starting with <code>1</code>.
     *
     * @return the line number, or <code>-1</code> if the line number is undefined.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the column number, starting with <code>1</code>.
     *
     * @return the column number, or <code>-1</code> if the column number is undefined.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns the length, in characters, of the segment pointed by this position; a length can be
     * <code>0</code> if the position describes a point, not a segment.
     *
     * @return the length, or <code>-1</code> if the exact position is undefined.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns a string representation of this position including all known parameters. Returns "<code>?</code>"
     * if the position is undefined.
     */
    @Override
    public String toString() {
        String sourceName = source.getName();
        if (line == -1) {
            return sourceName != null ? sourceName : "?";
        }

        StringBuilder bld = new StringBuilder(sourceName);
        bld.append('(');
        bld.append(line);
        if (column != -1) {
            bld.append(':');
            bld.append(column);
        }
        bld.append(')');
        return bld.toString();
    }
}
