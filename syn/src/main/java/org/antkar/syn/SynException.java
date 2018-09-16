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

/**
 * Common superclass for all SYN exception types.
 */
public class SynException extends Exception {
    private static final long serialVersionUID = 6627708705706922752L;

    public SynException() {
        super();
    }

    public SynException(String message, Throwable cause) {
        super(message, cause);
    }

    public SynException(String message) {
        super(message);
    }

    public SynException(Throwable cause) {
        super(cause);
    }
}
