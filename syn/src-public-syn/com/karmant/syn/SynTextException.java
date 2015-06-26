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
package com.karmant.syn;

/**
 * Text exception - an exception associated with a position in the input.
 */
public class SynTextException extends SynException {
    private static final long serialVersionUID = 2755948876076767196L;
    
    /** Text position. Can be <code>null</code>. */
    private final TextPos textPos;
    
    /** Original message. Can be <code>null</code>. */
    private final String originalMessage;

    /**
     * Constructs an exception with the specified input position and message.
     * 
     * @param textPos the input position; can be <code>null</code>.
     * @param message the message; can be <code>null</code>.
     */
    SynTextException(TextPos textPos, String message) {
        super(constructMessage(textPos, message));
        this.textPos = textPos;
        this.originalMessage = message;
    }

    /**
     * Constructs an exception with the specified input position, message and cause exception.
     * 
     * @param textPos the input position; can be <code>null</code>.
     * @param originalMessage the message; can be <code>null</code>.
     * @param cause the cause exception.
     */
    SynTextException(TextPos textPos, String originalMessage, Throwable cause) {
        super(constructMessage(textPos, originalMessage), cause);
        this.textPos = textPos;
        this.originalMessage = originalMessage;
    }
    
    /**
     * Returns the text position associated with the exception.
     * @return the text position.
     */
    public TextPos getTextPos() {
        return textPos;
    }
    
    /**
     * Returns the original exception message that was passed to a constructor. The message returned by
     * {@link Throwable#getMessage()} method differs from the original message, because it is prefixed
     * with the position information (file name, line number, etc.). 
     * @return the original message.
     */
    public String getOriginalMessage() {
        return originalMessage;
    }
    
    private static String constructMessage(TextPos pos, String originalMessage) {
        if (pos == null || pos.getSource() == null) {
            //Position is unknown. Return the original message without changes.
            return originalMessage;
        } else {
            //Add the position to the message.
            return pos + ": " + originalMessage;
        }
    }

}
