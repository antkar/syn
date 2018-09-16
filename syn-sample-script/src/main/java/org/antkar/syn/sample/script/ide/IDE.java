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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.antkar.syn.SynException;
import org.antkar.syn.sample.script.ScriptExecutor;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.util.NullInputStream;

/**
 * A simple IDE for the Script Language. Contains a source code editor with syntax coloring and
 * an output box where standard output and standard error streams are redirected to.
 */
public final class IDE {

    private static final String AUTOSAVE_FILE_NAME_PREFIX = "ScriptAutoSave";
    private static final String AUTOSAVE_FILE_NAME = AUTOSAVE_FILE_NAME_PREFIX + ".txt";
    private static final String AUTOSAVE_CHARSET = "UTF-8";

    private static final File AUTOSAVE_FILE = new File(AUTOSAVE_FILE_NAME);

    private static final String DEFAULT_SCRIPT =
            "import javax.swing.JOptionPane.*;\n" +
            "\n" +
            "showMessageDialog(null, 'Hello World', 'Message', INFORMATION_MESSAGE);\n";

    private final ScriptWindow window;

    private final InputStream redirectedStdin;
    private final PrintStream redirectedStdout;
    private final PrintStream redirectedStderr;

    private String savedSource;

    private IDE() {
        window = new ScriptWindow(this, ScriptExecutor.getScannerFactory());

        //Setup standard streams.
        redirectedStdin = NullInputStream.INSTANCE;
        WindowOutputStream stdout = new OutWindowOutputStream(window);
        WindowOutputStream stderr = new ErrWindowOutputStream(window);
        stdout.setPairedOut(stderr);
        stderr.setPairedOut(stdout);
        String charset = "ISO-8859-1";
        try {
            redirectedStdout = new PrintStream(stdout, true, charset);
            redirectedStderr = new PrintStream(stderr, true, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

        //Load the initial script source code.
        savedSource = loadScriptFromFile();
        if (savedSource == null) {
            savedSource = DEFAULT_SCRIPT;
        }
        window.setScriptText(savedSource);
    }

    /**
     * Executes the specified script in the IDE.
     */
    void executeScript(String source) throws SynException, SynsException {
        //Save current script source code.
        saveScript(source);

        //Execute the script.
        ScriptScope scope = ScriptScope.createRootScope();
        ScriptExecutor.execute(scope, source, redirectedStdin, redirectedStdout, redirectedStderr);
    }

    /**
     * Saves the specified script source code to the auto-save file.
     */
    synchronized void saveScript(String source) {
        if (!source.equals(savedSource)) {
            saveScriptToFile(source, AUTOSAVE_FILE);
            saveScriptToFile(source, getBackupFile());
            savedSource = source;
        }
    }

    /**
     * Starts the IDE. Shows the window.
     */
    static void start() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new IDE();
            }
        });
    }

    /**
     * Loads a script source code from the auto-save file. Returns <code>null</code> if problem
     * happens.
     */
    private static String loadScriptFromFile() {
        try (InputStream in = new FileInputStream(AUTOSAVE_FILE)) {
            Reader reader = new InputStreamReader(in, AUTOSAVE_CHARSET);
            StringBuilder bld = new StringBuilder();
            for (;;) {
                int k = reader.read();
                if (k == -1) {
                    break;
                }
                bld.append((char)k);
            }
            return bld.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Saves the specified script source code to the specified file.
     */
    private static void saveScriptToFile(String script, File file) {
        try (PrintWriter out = new PrintWriter(file, AUTOSAVE_CHARSET)) {
            out.print(script);
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            //ignore.
        }
    }

    /**
     * Returns a new backup file name.
     */
    private static File getBackupFile() {
        File dir = AUTOSAVE_FILE.getAbsoluteFile().getParentFile();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String name = AUTOSAVE_FILE_NAME_PREFIX + "_" + dateFormat.format(new Date()) + ".txt";
        return new File(dir, name);
    }
}
