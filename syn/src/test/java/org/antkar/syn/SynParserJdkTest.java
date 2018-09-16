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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test that parser JDK source codes.
 */
public class SynParserJdkTest extends Assert {
    private static final String JDK_SRC_FILE_NAME = "src.zip";

    static final File JDK_SRC_FILE;

    static {
        String javaPath = System.getProperty("java.home");
        File javaDir = new File(javaPath);
        File jdkDir = javaDir.getParentFile();
        JDK_SRC_FILE = new File(jdkDir, JDK_SRC_FILE_NAME);
    }

    private int fileNo;
    private int totalLines;
    private long totalChars;
    private List<SynException> errors = new ArrayList<>();

    @Test
    @Ignore
    public void testParseJdk() throws Exception {
        File zipFile = JDK_SRC_FILE;
        int fileCount = calcJavaFileCount(zipFile);

        SynParser synParser = SynParserSimpleTest.createParser("SynParserJavaTest_grammar.txt");
        synParser.setFailOnAmbiguity(true);

        long time = System.currentTimeMillis();
        processZipFile(zipFile, fileCount, synParser);
        time = System.currentTimeMillis() - time;

        for (SynException e : errors) {
            e.printStackTrace(System.out);
        }
        for (SynException e : errors) {
            System.out.println(e);
        }

        System.out.println(String.format(Locale.US,
                "Summary: %,d errors; %,d files; %,d lines; %,d characters; %,d s",
                errors.size(),
                fileNo,
                totalLines,
                totalChars,
                (time / 1000)));
        assertEquals(0, errors.size());

    }

    private void processZipFile(File zipFile, int fileCount, SynParser synParser) throws IOException {
        try (
                InputStream in = new FileInputStream(zipFile);
                ZipInputStream zin = new ZipInputStream(in))
        {
            for (;;) {
                ZipEntry ze = zin.getNextEntry();
                if (ze == null) {
                    break;
                }
                if (isJavaZipEntry(ze)) {
                    String fileName = ze.getName();
                    parseJavaFile(fileCount, synParser, zin, fileName);
                }
            }
        }
    }

    private void parseJavaFile(int fileCount, SynParser synParser, InputStream in, String fileName) {
        try {
            ++fileNo;
            System.out.println(String.format("%d/%d %s", fileNo, fileCount, fileName));
            SourceDescriptor sourceDescriptor = new StringSourceDescriptor(fileName);

            Reader reader = new InputStreamReader(in);
            SynResult result = synParser.parse("goal", reader, sourceDescriptor);

            totalLines += result.getLineCount();
            totalChars += result.getCharCount();
        } catch (SynException e) {
            errors.add(e);
        }
    }

    private static int calcJavaFileCount(File zipFile) throws IOException {
        System.out.println("Calculating .java files count...");

        int count = 0;

        try (
                InputStream in = new FileInputStream(zipFile);
                ZipInputStream zin = new ZipInputStream(in))
        {
            for (ZipEntry entry = zin.getNextEntry(); entry != null; entry = zin.getNextEntry()) {
                if (isJavaZipEntry(entry)) {
                    ++count;
                }
            }
        }

        System.out.println("done: " + count);
        return count;
    }

    static boolean isJavaZipEntry(ZipEntry entry) {
        return !entry.isDirectory() && entry.getName().endsWith(".java");
    }
}
