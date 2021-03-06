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
package org.antkar.syn.sample.script.rt;

import org.antkar.syn.TextPos;

/**
 * A script error associated with a source code position.
 */
public class TextSynsException extends SynsException {
    private static final long serialVersionUID = 6304936443784708827L;

    private final String originalMessage;
    private final TextPos textPos;

    public TextSynsException(String message, TextPos textPos) {
        super(textPos + ": " + message);
        this.originalMessage = message;
        this.textPos = textPos;
    }

    public TextSynsException(Throwable cause, TextPos textPos) {
        this(cause.getMessage(), cause, textPos);
    }

    public TextSynsException(String message, Throwable cause, TextPos textPos) {
        super(textPos + ": " + message, cause);
        this.originalMessage = message;
        this.textPos = textPos;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public TextPos getTextPos() {
        return textPos;
    }
}
