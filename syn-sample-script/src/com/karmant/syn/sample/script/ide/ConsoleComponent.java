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
import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

/**
 * IDE Console visual component. Displays standard output and error output in different colors.
 */
class ConsoleComponent {

    private final JTextPane textPane;
    private final StyledDocument doc;
    private final JPanel panel;
    
    private final AttributeSet outStyle;
    private final AttributeSet errStyle;
    
    ConsoleComponent() {
        textPane = new JTextPane();
        textPane.setEditable(false);
        doc = textPane.getStyledDocument();
        
        panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = UIUtil.createTextPaneScrollPane(textPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        Font font = UIManager.getFont("TextArea.font");
        outStyle = UIUtil.createTextAttributes(font, Color.BLACK, false, false);
        errStyle = UIUtil.createTextAttributes(font, Color.RED, false, false);
    }
    
    /**
     * Returns the underlying Swing component.
     */
    JComponent getComponent() {
        return panel;
    }
    
    /**
     * Appends the specified string as a standard output message.
     */
    void stdOut(String str) {
        append(str, outStyle);
    }
    
    /**
     * Appends the specified string as an error message.
     */
    void stdErr(String str) {
        append(str, errStyle);
    }
    
    /**
     * Clears the console.
     */
    void clear() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            //ignore.
        }
    }
    
    private void append(String str, AttributeSet attributes) {
        try {
            doc.insertString(doc.getLength(), str, attributes);
        } catch (BadLocationException e) {
            //ignore
        }
    }
}
