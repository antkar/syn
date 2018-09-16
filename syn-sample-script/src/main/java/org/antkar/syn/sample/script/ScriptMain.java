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
package org.antkar.syn.sample.script;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.antkar.syn.binder.SynBinder;
import org.antkar.syn.sample.script.schema.Script;

/**
 * Script Language interpreter application entry point. Executes script files specified in the command line.
 */
public final class ScriptMain {
    private ScriptMain(){}

    public static void main(String[] args) throws Exception {
        //Parse command line arguments.
        List<File> files = new ArrayList<>();
        List<String> arguments = new ArrayList<>();
        parseArguments(args, files, arguments);

        //Check if script files were specified.
        if (files.isEmpty()) {
            System.out.println("Usage: ScriptMain FILE_NAME+ [-args ARGUMENT+]");
            System.exit(1);
        }

        //Parse scripts.
        SynBinder<Script> binder = ScriptExecutor.getSynBinder();

        List<Script> scripts = new ArrayList<>();
        for (File file : files) {
            Script script = binder.parse(file);
            scripts.add(script);
        }

        //Execute scripts.
        Script.execute(scripts, arguments);
    }

    /**
     * Parses command line arguments.
     */
    private static void parseArguments(String[] args, List<File> files, List<String> arguments) {
        //Parse file names.
        int ofs = 0;
        while (ofs < args.length && !"-args".equals(args[ofs])) {
            files.add(new File(args[ofs]));
            ++ofs;
        }

        //Parse options.
        if (ofs < args.length) {
            ++ofs;
            while (ofs < args.length) {
                arguments.add(args[ofs]);
                ++ofs;
            }
        }
    }
}
