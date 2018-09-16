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

import org.antkar.syn.SynException;

/**
 * A primitive scanner that skips all blank tokens and returns non-blank ones. Contains two internal scanners:
 * one for blank tokens, and one for the others. Blank tokens may be white spaces and comments.
 */
final class BlankSkipScanner implements IPrimitiveScanner {
    private final IPrimitiveScanner blankScanner;
    private final IPrimitiveScanner scanner;

    BlankSkipScanner(IPrimitiveScanner blankScanner, IPrimitiveScanner scanner) {
        this.blankScanner = blankScanner;
        this.scanner = scanner;
    }

    @Override
    public IPrimitiveResult scan(PrimitiveContext context) throws SynException {
        boolean blankSkipped = false;
        while (blankScanner.scan(context) != null) {
            blankSkipped = true;
        }
        if (blankSkipped) {
            context.startToken();
        }
        return scanner.scan(context);
    }
}
