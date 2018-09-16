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

import org.antkar.syn.internal.TokenTypeProcessor;

/**
 * Token type.
 *
 * <p>A <i>literal</i> token type denotes value literal tokens, like identifiers or integer numbers.
 * A literal token has an associated value of a particular type.
 * Each literal token has a single corresponding {@link TokenDescriptor token descriptor} instance.</p>
 *
 * <p>A <i>custom</i> token type corresponds to user-defined tokens like keywords and key-characters.</p>
 */
public enum TokenType {
    /** End-of-file. */
    END_OF_FILE(false, false)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processEndOfFile();
        }
    },
    /** Identifier. */
    ID(true, false)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processIdentifier();
        }
    },
    /** Integer literal. */
    INTEGER(true, false)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processIntegerLiteral();
        }
    },
    /** Floating-point literal. */
    FLOAT(true, false)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processFloatingPointLiteral();
        }
    },
    /** String literal. */
    STRING(true, false)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processStringLiteral();
        }
    },
    /** Custom keyword. */
    KEYWORD(false, true)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processKeyword();
        }
    },
    /** Custom key-character. */
    KEYCHAR(false, true)
    {
        @Override
        public <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException {
            return processor.processKeyChar();
        }
    };

    private final boolean literal;
    private final boolean custom;

    private TokenType(boolean literal, boolean custom) {
        assert !literal || !custom;
        this.literal = literal;
        this.custom = custom;
    }

    /**
     * Checks if this token type represents a literal token.
     * @return <code>true</code> for a literal token type.
     */
    public boolean isLiteral() {
        return literal;
    }

    /**
     * Checks if this token type represents a custom token.
     * @return <code>true</code> for a custom token type.
     */
    boolean isCustom() {
        return custom;
    }

    /**
     * Each token type invokes a corresponding method of the processor (visitor pattern).
     */
    public abstract <T> T invokeProcessor(TokenTypeProcessor<T> processor) throws SynException;
}
