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
package com.karmant.syn.sample.script.ide;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.karmant.syn.sample.script.ScannerFactory;

/**
 * Script IDE window. Contains a source code editor and a console output box.
 */
class ScriptWindow {
    
    private final IDE ide;
    
    private final JFrame frame;
    private final SourceCodeEditor sourceCodeEditor;
    private final ConsoleComponent console;
    private final JButton btnExecute;
    private final JSplitPane splitPane;

    ScriptWindow(IDE ide, ScannerFactory scannerFactory) {
        this.ide = ide;
        
        //Create components.
        frame = new JFrame("Script");
        sourceCodeEditor = new SourceCodeEditor(scannerFactory);
        console = new ConsoleComponent();
        btnExecute = new JButton("Execute");
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        JPanel panel = createMainPanel();
        
        //Initialize listeners.
        initListeners();
        
        //Setup frame.
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        //Divide the space between source code editor and console.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                splitPane.setDividerLocation(0.5);
            }
        });
    }
    
    /**
     * Installs components' listeners.
     */
    private void initListeners() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String source = sourceCodeEditor.getText();
                ide.saveScript(source);
            }
        });
        
        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnExecute();
            }
        });
    }

    /**
     * Creates window's main panel.
     */
    private JPanel createMainPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnExecute);
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(buttonPanel, BorderLayout.NORTH);
        outputPanel.add(console.getComponent(), BorderLayout.CENTER);
        
        splitPane.add(sourceCodeEditor.getComponent());
        splitPane.add(outputPanel);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * "Execute" button clicked.
     */
    private void btnExecute() {
        final String source = sourceCodeEditor.getText();
        console.clear();
        btnExecute.setEnabled(false);
        
        //Run script in a new thread.
        Thread thread = new Thread("ScriptThread") {
            @Override
            public void run() {
                try {
                    ide.executeScript(source);
                } catch (Exception e) {
                    executionFailed(e);
                } finally {
                    executionFinished();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Script execution failed.
     */
    private void executionFailed(Exception e) {
        e.printStackTrace(System.out);
        
        final StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);
        e.printStackTrace(pwriter);
        pwriter.flush();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                console.stdErr(writer.toString());
            }
        });
    }
    
    /**
     * Script execution finished (successfully or not).
     */
    private void executionFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                btnExecute.setEnabled(true);
            }
        });
    }
    
    /**
     * Sets the script source code.
     */
    void setScriptText(String text) {
        sourceCodeEditor.setText(text);
    }
    
    /**
     * Returns the current script source code in the editor.
     */
    String getScriptText() {
        return sourceCodeEditor.getText();
    }
    
    /**
     * Writes a string into the console box as a standard output.
     */
    void writeOut(String str) {
        console.stdOut(str);
    }
    
    /**
     * Writes a string into the console box as a standard error.
     */
    void writeErr(String str) {
        console.stdErr(str);
    }
}
