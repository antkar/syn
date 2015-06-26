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
package com.karmant.syn;

import java.io.IOException;
import java.io.Reader;

/**
 * Token stream implementation. Combines different {@link IPrimitiveScanner}s to scan different types
 * of tokens.
 */
class DefaultTokenStream implements TokenStream {
    /** The compound primitive scanner. */
    private final IPrimitiveScanner primitiveScanner;
    
    private final PrimitiveContext primitiveContext;
    
    /** Current token result. */
    private IPrimitiveResult tokenResult;
    
    /** Current token descriptor. */
    private TokenDescriptor tokenDescriptor;
    
    /** Current token node. */
    private TerminalNode token;
    
    DefaultTokenStream(SourceDescriptor sourceDescriptor, ScannerConfiguration config, Reader reader)
            throws SynException
    {
        assert config != null;
        assert sourceDescriptor != null;
        assert reader != null;
        
        CharStream charStream = new CharStream(reader);
        
        LookaheadCharStream dblCharStream;
        try {
            dblCharStream = new LookaheadCharStream(charStream);
        } catch (IOException e) {
            throw new SynException(e);
        }
        
        primitiveScanner = createPrimitiveScanner(config);
        primitiveContext = new PrimitiveContext(sourceDescriptor, dblCharStream);
    }

    @Override
    public void nextToken() throws SynException {
        primitiveContext.startToken();
        
        try {
            tokenResult = primitiveScanner.scan(primitiveContext);
            if (tokenResult == null) {
                //All primitive scanners failed to scan a token. Lexical error.
                TextPos pos = primitiveContext.getCurrentCharPos();
                throw new SynLexicalException(pos, "Lexical error");
            }
        } catch (SynException e) {
            //Skip a character in order to allow recovering.
            try {
                primitiveContext.next();
            } catch (SynException e2) {
                //ignore.
            }
            throw e;
        }
        
        //Scan successful.
        tokenDescriptor = tokenResult.getTokenDescriptor();
        assert tokenDescriptor != null;
        token = null;
    }
    
    @Override
    public TokenDescriptor getTokenDescriptor() {
        assert tokenDescriptor != null;
        return tokenDescriptor;
    }
    
    /**
     * Returns the {@link TerminalNode} describing the current token.
     */
    TerminalNode getTokenNode() {
        if (token == null) {
            //Has not been created yet for the current token. Create.
            assert tokenResult != null;
            PosBuffer pos = primitiveContext.getStartPosBuffer();
            token = tokenResult.createTokenNode(pos);
            assert token != null;
        }
        return token;
    }
    
    /**
     * Returns the position of the first character of the current token.
     */
    TextPos getTokenPos() {
        TextPos startPos = primitiveContext.getStartPos();
        return startPos;
    }
    
    @Override
    public int getTokenStartOffset() {
        return primitiveContext.getStartOffset();
    }
    
    @Override
    public int getTokenEndOffset() {
        return primitiveContext.getEndOffset();
    }
    
    @Override
    public int getCurrentOffset() {
        return primitiveContext.getCurrentOffset();
    }

    /**
     * Creates the compound primitive scanner able to scan all supported types of tokens.
     */
    private static IPrimitiveScanner createPrimitiveScanner(ScannerConfiguration config) {

        //Create a blank scanner - a scanner that accepts white spaces and comments.
        IPrimitiveScanner whiteSpaceScanner = new BlankScanner();
        IPrimitiveScanner singleLineCommentScanner = new SingleLineCommentScanner();
        IPrimitiveScanner multipleLineCommentScanner = new MultipleLineCommentScanner();
        IPrimitiveScanner blankScanner = new CompoundScanner(
                whiteSpaceScanner,
                singleLineCommentScanner,
                multipleLineCommentScanner);
        
    
        //Numeric literal scanner. If floating-point literals are not used in the grammar, allow
        //only integer literals.
        boolean floatingPoint = config.isFloatingPoint();
        IPrimitiveScanner numberScanner = floatingPoint ? new NumberScanner() : new IntegerNumberScanner();

        //Other scanners.
        IPrimitiveScanner eofScanner = new EofScanner();
        IPrimitiveScanner keywordScanner = new KeywordScanner(config);
        IPrimitiveScanner keycharScanner = new KeycharScanner(config);
        IPrimitiveScanner stringScanner = new StringScanner();
        
        //Compound scanner for all non-blank tokens.
        IPrimitiveScanner nonBlankScanner = new CompoundScanner(
                eofScanner,
                keywordScanner,
                stringScanner,
                numberScanner,
                keycharScanner);
        
        //Resulting scanner.
        IPrimitiveScanner result = new BlankSkipScanner(blankScanner, nonBlankScanner);
        return result;
    }
    
    @Override
    public String toString() {
        return primitiveContext + "";
    }
}
