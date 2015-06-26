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

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * UI utility methods.
 */
final class UIUtil {
    private UIUtil(){}
    
    /**
     * Creates a default scroll pane for a text pane.
     */
    static JScrollPane createTextPaneScrollPane(JTextPane textPane) {
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane);
        JScrollPane scrollPane = new JScrollPane(noWrapPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(textPane.getFont().getSize() * 2);
        return scrollPane;
    }
    
    /**
     * Creates text style attributes with the specified properties.
     */
    static AttributeSet createTextAttributes(Font baseFont, Color color, boolean bold, boolean italic) {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        if (baseFont != null) {
            StyleConstants.setFontFamily(attrs, baseFont.getFamily());
            StyleConstants.setFontSize(attrs, baseFont.getSize());
        }
        StyleConstants.setForeground(attrs, color);
        StyleConstants.setBold(attrs, bold);
        StyleConstants.setItalic(attrs, italic);
        return attrs;
    }
}
