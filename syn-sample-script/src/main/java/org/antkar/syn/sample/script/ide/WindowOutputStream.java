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
package org.antkar.syn.sample.script.ide;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.SwingUtilities;

/**
 * Output stream that redirects output to a {@link ScriptWindow}'s console box.
 */
abstract class WindowOutputStream extends OutputStream {
    private final ScriptWindow window;
    private final StringBuilder bld;

    private OutputStream otherOut;

    WindowOutputStream(ScriptWindow window) {
        this.window = window;
        bld = new StringBuilder();
    }

    /**
     * Sets the paired output. A paired output stream is flushed when a byte is written into this
     * stream. This is necessary to avoid mixing of standard output and standard error fragments.
     */
    final void setPairedOut(OutputStream otherOut) {
        this.otherOut = otherOut;
    }

    @Override
    public final void write(int b) throws IOException {
        synchronized (window) {
            if (otherOut != null) {
                otherOut.flush();
            }

            bld.append((char)b);
            if (b == '\n' || bld.length() >= 100) {
                //Automatically flush on a new line or if the length of the buffer is big enough.
                flush();
            }
        }
    }

    @Override
    public final void flush() {
        synchronized (window) {
            if (bld.length() > 0) {
                final String str = bld.toString();
                bld.setLength(0);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        append(window, str);
                    }
                });
            }
        }
    }

    /**
     * Append the specified string to a {@link ScriptWindow}'s standard output or standard error.
     */
    abstract void append(ScriptWindow win, String str);
}
