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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import com.karmant.syn.SynException;
import com.karmant.syn.SynLexicalException;
import com.karmant.syn.TokenDescriptor;
import com.karmant.syn.TokenStream;
import com.karmant.syn.TokenType;
import com.karmant.syn.sample.script.ScannerFactory;

/**
 * Source Code Editor visual component. The main feature is highlighting of different types of tokens
 * with different text styles.
 */
class SourceCodeEditor {
    private final ScannerFactory scannerFactory;
    
    private final JTextPane textPane;
    private final JScrollPane scrollPane;
    
    private final AttributeSet defaultAttributes;
    private final AttributeSet identifierAttributes;
    private final AttributeSet keywordAttributes;
    private final AttributeSet keycharAttributes;
    private final AttributeSet numberAttributes;
    private final AttributeSet stringAttributes;
    private final AttributeSet commentAttributes;
    
    private boolean manualUpdating;
    
    /**
     * A byte buffer used to support incremental highlighting. The size of the buffer is the same
     * as the size of the actual text, in characters. A byte at a particular offset in the buffer has
     * value 1 if and only if that offset denotes the start of a token. Otherwise, the byte has value 0.
     * When a new text fragment is inserted to or deleted from the document, the byte buffer allows
     * to determine which fragment of the text has to be highlighted from scratch.
     */
    private final ByteBuffer byteBuffer;
    
    SourceCodeEditor(ScannerFactory scannerFactory) {
        this.scannerFactory = scannerFactory;

        textPane = new JTextPane();
        scrollPane = UIUtil.createTextPaneScrollPane(textPane);

        initListener();
        
        Color darkGreen = Color.GREEN.darker().darker();

        //Define text attributes for different types of tokens.
        Font font = UIManager.getFont("TextArea.font");
        defaultAttributes = UIUtil.createTextAttributes(font, Color.BLACK, false, false);
        identifierAttributes = defaultAttributes;
        keywordAttributes = UIUtil.createTextAttributes(font, new Color(0x880033), true, false);
        keycharAttributes = defaultAttributes;
        numberAttributes = defaultAttributes;
        stringAttributes = UIUtil.createTextAttributes(font, Color.BLUE, false, false);
        commentAttributes = UIUtil.createTextAttributes(font, darkGreen, false, true);
        
        byteBuffer = new ByteBuffer();
    }
    
    /**
     * Installs components' listeners.
     */
    private void initListener() {
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!manualUpdating) {
                    int offset = e.getOffset();
                    handleInsert(offset, offset + e.getLength());
                }
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!manualUpdating) {
                    int offset = e.getOffset();
                    handleRemove(offset, offset + e.getLength());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (!manualUpdating) {
                    int offset = e.getOffset();
                    handleChange(offset, offset + e.getLength());
                }
            }
        });
    }
    
    /**
     * Sets the text.
     */
    void setText(String text) {
        manualUpdating = true;
        try {
            //Set the text to the document.
            StyledDocument doc = textPane.getStyledDocument();
            doc.remove(0, doc.getLength());
            doc.insertString(0, text, defaultAttributes);
            
            //Highlight the text.
            highlight();
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        } finally {
            manualUpdating = false;
        }
        textPane.setCaretPosition(0);
    }

    /**
     * Returns the current text.
     */
    String getText() {
        StyledDocument doc = textPane.getStyledDocument();
        return getDocText(doc, 0, doc.getLength());
    }
    
    /**
     * Returns the associated Swing component.
     */
    JComponent getComponent() {
        return scrollPane;
    }
    
    /**
     * Highlights tokens in the current document.
     */
    private void highlight() throws BadLocationException {
        StyledDocument doc = textPane.getStyledDocument();
        String text = doc.getText(0, doc.getLength());
        
        int length = text.length();
        byteBuffer.clear();
        byteBuffer.insert(0, length, 0);
        highlight(doc, text, 0, length);
    }

    /**
     * Highlights the specified fragment of the current document, leaving the style of the rest of the
     * document unchanged.
     */
    private void rehighlight(final int start, final int end) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                manualUpdating = true;
                try {
                    int tkStart = findTokenStart(start);
                    StyledDocument doc = textPane.getStyledDocument();
                    //TODO getting text from the current position till the end of the document is not
                    //the most optimal solution, since the text getting operation of a document may be
                    //not straightforward (because of possible complex document structure). It would be
                    //better to use a special Reader to read characters from the document by portions.
                    String text = getDocText(doc, tkStart, doc.getLength() - tkStart);
                    highlight(doc, text, tkStart, end);
                } finally {
                    manualUpdating = false;
                }
            }
        });
    }

    /**
     * Highlights the specified fragment of a document.
     */
    private void highlight(StyledDocument doc, String text, int absTextStart, int absTextEnd) {
        TokenStream stream = scannerFactory.createTokenStream(text);
        
        FragmentHighlighter highlighter = new FragmentHighlighter(doc, absTextStart);
        
        //Highlight tokens one by one.
        int absLastTokenEnd = absTextStart;
        for (;;) {
            //Read next token.
            TokenType token;
            try {
                token = scanToken(stream, highlighter, absLastTokenEnd);
            } catch (Exception e) {
                //On lexical error - stop highlighting.
                break;
            }

            //Determine the start and the end offset of the token.
            int absTokenStart = absTextStart + stream.getTokenStartOffset();
            int absTokenEnd = absTextStart + stream.getTokenEndOffset();

            if (absTokenStart > absLastTokenEnd) {
                //There is a gap between the end of the previous token and the start of the current one.
                //This gap can contain white spaces and comments. Comments are not considered tokens
                //and ignored by the token stream, but they have to be highlighted by a corresponding
                //text style.
                highlightBlank(highlighter, text, absTextStart, absLastTokenEnd, absTokenStart);
                
                if (isTokenBoundary(absTextEnd, absTokenStart)) {
                    //Old token boundary reached - highlighting finished.
                    break;
                }
            }
            
            //Stop, if end of the input is reached.
            if (TokenType.END_OF_FILE == token) {
                break;
            }
            
            //Highlight the current token.
            highlightToken(highlighter, absTokenStart, absTokenEnd, token);
            
            if (isTokenBoundary(absTextEnd, absTokenEnd)) {
                //Old token boundary reached - highlighting finished.
                break;
            }
            
            absLastTokenEnd = absTokenEnd;
        }

        //Highlight the last fragment, since the highlighter defers text style modification.
        highlighter.finish();
    }
    
    /**
     * Scans the next token from a token stream, returns the token's type.
     */
    private TokenType scanToken(TokenStream stream, FragmentHighlighter highlighter, int absLastTokenEnd)
            throws SynException
    {
        try {
            stream.nextToken();
            TokenDescriptor tokenDesc = stream.getTokenDescriptor();
            return tokenDesc.getType();
        } catch (SynLexicalException e) {
            return null;
        } catch (SynException | RuntimeException e) {
            //For simplicity, stopping highlighting on a lexical error.
            highlighter.highlight(defaultAttributes, byteBuffer.size() - absLastTokenEnd);
            byteBuffer.set(absLastTokenEnd, byteBuffer.size(), 0);
            throw e;
        }
    }

    /**
     * Returns <code>true</code> if the given position is out of the modified text fragment range
     * and matches a token boundary.
     */
    private boolean isTokenBoundary(int absTextEnd, int absPos) {
        return absPos >= absTextEnd && absPos < byteBuffer.size() && byteBuffer.get(absPos) != 0;
    }

    /**
     * Highlights a token according to its type.
     */
    private void highlightToken(FragmentHighlighter highlighter, int absStart, int absEnd, TokenType token) {
        AttributeSet attrs = getTokenTextAttributes(token);
        highlightFragment(highlighter, absStart, absEnd, attrs);
    }

    /**
     * Highlights a fragment and updates the byte buffer correspondingly.
     */
    private void highlightFragment(
            FragmentHighlighter highlighter,
            int absStart,
            int absEnd,
            AttributeSet attrs)
    {
        byteBuffer.set(absStart, 1);
        byteBuffer.set(absStart + 1, absEnd, 0);
        highlighter.highlight(attrs, absEnd - absStart);
    }

    /**
     * Returns text style for the specified token type.
     */
    private AttributeSet getTokenTextAttributes(TokenType token) {
        if (token == null) {
            return defaultAttributes;
        }
        
        switch (token) {
        case ID:
            return identifierAttributes;
        case KEYWORD:
            return keywordAttributes;
        case KEYCHAR:
            return keycharAttributes;
        case INTEGER:
        case FLOAT:
            return numberAttributes;
        case STRING:
            return stringAttributes;
        default:
            return defaultAttributes;
        }
    }

    /**
     * Highlights blank text fragment. A blank fragment is a sequence of white spaces and comments which
     * are skipped by token stream.
     */
    private void highlightBlank(
            FragmentHighlighter highlighter,
            String text,
            int absTextStart,
            int absTokenStart,
            int absTokenEnd)
    {
        //Update the byte buffer.
        byteBuffer.set(absTokenStart, 1);
        byteBuffer.set(absTokenStart + 1, absTokenEnd, 0);
        
        if (absTokenEnd - absTokenStart > 2) {
            //If the blank fragment contains non-white space characters, that characters must be
            //a comment and have to be highlighted correspondingly.
            int relTokenStart = absTokenStart - absTextStart;
            int relTokenEnd = absTokenEnd - absTextStart;
            highlightComment(highlighter, text, relTokenStart, relTokenEnd);
        } else {
            highlighter.highlight(null, absTokenEnd - absTokenStart);
        }
    }

    /**
     * Highlights non-white space characters in the given text fragment with a comment text style.
     */
    private void highlightComment(
            FragmentHighlighter highlighter,
            String text,
            int relStart,
            int relEnd)
    {
        int relCommentStart = relStart;
        int relCommentEnd = relEnd;
        
        //Find the start position of a non-white space sequence.
        while (relCommentStart < relCommentEnd
                && Character.isWhitespace(text.charAt(relCommentStart)))
        {
            ++relCommentStart;
        }
        
        //Find the end position of a non-white space sequence.
        while (relCommentStart < relCommentEnd
                && Character.isWhitespace(text.charAt(relCommentEnd - 1)))
        {
            --relCommentEnd;
        }
        
        //If the sequence is not empty, highlight it as a comment.
        if (relCommentStart < relCommentEnd) {
            if (relStart < relCommentStart) {
                //Highlight the fragment before the sequence as a whitespace.
                highlighter.highlight(null, relCommentStart - relStart);
            }
            highlighter.highlight(commentAttributes, relCommentEnd - relCommentStart);
            if (relCommentEnd < relEnd) {
                //Highlight the fragment after the sequence as a whitespace.
                highlighter.highlight(commentAttributes, relEnd - relCommentEnd);
            }
        } else {
            //All characters in the specified range are white spaces. Do not highlight them.
            highlighter.highlight(null, relEnd - relStart);
        }
    }
    
    /**
     * Highlights changed text fragment after an insert operation.
     */
    private void handleInsert(int start, int end) {
        byteBuffer.insert(start, end, 0);
        rehighlight(start, end);
    }
    
    /**
     * Highlight changed text fragment after a remove operation.
     */
    private void handleRemove(int start, int end) {
        byteBuffer.remove(start, end);
        //Passing 0-length range - the token which contains it must be rescanned.
        rehighlight(start, start);
    }
    
    private void handleChange(int start, int end) {
        //Seems to not be called for typing events. Ignoring must not cause serious problems.
    }
    
    /**
     * Find the start position of the first token, starting from the specified position.
     */
    private int findTokenStart(int pos) {
        if (pos > 0) {
            --pos;
        }
        while (pos > 0 && byteBuffer.get(pos) == 0) {
            --pos;
        }
        return pos;
    }
    
    /**
     * Returns a fragment of the document's text.
     */
    private static String getDocText(StyledDocument doc, int offset, int length) {
        try {
            return doc.getText(offset, length);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
    }
}
