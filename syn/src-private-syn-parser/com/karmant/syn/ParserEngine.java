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

import java.util.Collection;
import java.util.List;

/**
 * Parser Engine. Whenever {@link SynParser#parse(String, java.io.Reader)} method is invoked, it creates a
 * new instance of {@link ParserEngine} to do the job.
 */
class ParserEngine {
    private final DefaultTokenStream tokenStream;
    private final ParserState startState;
    private final boolean failOnAmbiguity;
    private final StacksList stacksList;
    
    ParserEngine(
            DefaultTokenStream tokenStream,
            ParserConfiguration config,
            ParserState startState,
            boolean failOnAmbiguity)
    {
        assert tokenStream != null;
        assert config != null;
        assert startState != null;
        
        this.tokenStream = tokenStream;
        this.startState = startState;
        this.failOnAmbiguity = failOnAmbiguity;
        
        stacksList = new StacksList();
    }
    
    /**
     * Parses the input, returns the parser result.
     */
    SynResult parse() throws SynException {
        ParserStackElement resultElement;
        
        //Add the start stack into the set of stacks.
        addStartStack();
        
        //Parse input tokens.
        for (;;) {
            //Reduce all stacks.
            resultElement = reduceCurrentStacks();
            
            //Read the next token.
            tokenStream.nextToken();
            TokenDescriptor token = tokenStream.getTokenDescriptor();
            TokenType tokenType = token.getType();
            
            if (tokenType == TokenType.END_OF_FILE && resultElement != null) {
                //End-of-file which was expected by the grammar. Successful completion.
                break;
            }
            
            //Shift by the current token. If the token is end-of-file and it is unexpected at this
            //point, the shifting will fail and throw an appropriate exception.
            TerminalNode node = tokenStream.getTokenNode();
            shiftToNextState(token, node);
        }
        
        assert resultElement != null;
        SynResult result = makeResult(resultElement);
        
        return result;
    }
    
    /**
     * Creates a parser result from a parser stack.
     */
    private SynResult makeResult(ParserStackElement resultElement) {
        assert resultElement != null;
        
        //Create the root SynNode.
        IParserNode resultNode = resultElement.createParserNode();
        SynNode rootNode = resultNode == null ? null : resultNode.createUserNode();
        
        //Prepare statistical information.
        TextPos textPos = tokenStream.getTokenPos();
        int nChars = textPos.getOffset();
        int nLines = textPos.getLine();
        SourceDescriptor sourceDescriptor = textPos.getSource();
        
        //Create a result object.
        SynResult result = new SynResult(sourceDescriptor, rootNode, nLines, nChars);
        return result;
    }
    
    /**
     * Adds the start stack to the set of stacks.
     */
    private void addStartStack() {
        ParserStackElement stackElement = new StartParserStackElement(startState);
        ParserStack stack = new ParserStack(null, stackElement, stackElement.getDepth());
        stacksList.add(stack);
    }
    
    /**
     * Reduces all the stacks in the current stacks set, adding new stacks to the set until new reductions
     * are possible.
     */
    private ParserStackElement reduceCurrentStacks() throws SynAmbiguityException {
        for (int pos = 0; pos < stacksList.size(); ++pos) {
            ParserStack stack = stacksList.getByPos(pos);
            ParserState state = stack.getTop().getState();
            if (!state.isFinal() && !stack.isDeleted()) {
                reduceStack(stack);
            }
        }
        
        ParserStackElement resultElement = getResultElement();
        return resultElement;
    }

    /**
     * Returns a result stack element, if the start nonterminal was reduced during the last reduction
     * operation. Otherwise, returns <code>null</code>.
     */
    private ParserStackElement getResultElement() {
        ParserStack stack = stacksList.getResultStack();
        return stack == null ? null : stack.getTop();
    }
    
    /**
     * Reduces the specified stack using all possible productions, adding new stacks to the stacks list.
     */
    private void reduceStack(ParserStack stack) throws SynAmbiguityException {
        ParserStackElement stackTop = stack.getTop();
        ParserState state = stackTop.getState();
        for (ParserProduction production : state.getReduceProductions()) {
            reduceProduction(stack, production);
        }
    }
    
    /**
     * Reduces the specified stack using the specified production.
     */
    private void reduceProduction(ParserStack stack, ParserProduction production) throws SynAmbiguityException {
        ParserStackElement nextElement = getReducedStack(stack, production);
        
        int reduceDepth = Math.min(nextElement.getDepth(), stack.getReduceDepth());
        ParserStack nextStack = new ParserStack(stack, nextElement, reduceDepth);
        
        //Add the new stack to the list of stacks.
        ParserStack existingStack = stacksList.getByTop(nextElement);
        if (existingStack != null) {
            if (toReplaceExistingStack(existingStack, nextStack)) {
                existingStack.delete();
                stacksList.replace(nextStack);
            }
        } else if (!isCycledStack(nextStack)) {
            stacksList.add(nextStack);
        }
    }
    
    /**
     * Decides whether an existing stack has to be replaced by a new equal stack, or the new stack has to
     * be rejected.
     * 
     * @return <code>true</code> if the existing stack has to be replaced.
     */
    private boolean toReplaceExistingStack(ParserStack existingStack, ParserStack newStack)
            throws SynAmbiguityException
    {
        if (existingStack.isDeleted()) {
            //Existing stack is deleted - replace it.
            return true;
        }
        if (isRecursiveStack(newStack)) {
            //New stack is recursive - reject.
            return false;
        }
        
        //Ambiguity.
        if (failOnAmbiguity) {
            throw ParserEngineHelper.createAmbiguityException(tokenStream.getTokenPos(), existingStack, newStack);
        }
        
        //Choose a better stack with the help of Ambiguity Comparator.
        return AmbiguityComparator.compare(newStack, existingStack) > 0;
    }

    /**
     * Produces a reduced stack from an original stack.
     */
    private ParserStackElement getReducedStack(ParserStack stack, ParserProduction production) {
        int length = production.getLength();
        ParserStackElement stackTop = stack.getTop();
        ParserStackElement element = stackTop.getDeep(length);
        ParserStackElement nextElement = element.nextNt(production, stackTop);
        assert nextElement != null;
        return nextElement;
    }
    
    /**
     * Shifts all stacks by the specified token. If no stacks accept the token, a syntax exception is thrown.
     */
    private void shiftToNextState(TokenDescriptor token, TerminalNode node) throws SynException {
        List<ParserStack> prevStacks = stacksList.copyAndClear();
        
        //Shift all stacks.
        for (ParserStack currentStack : prevStacks) {
            if (!currentStack.isDeleted()) {
                ParserStackElement currentTop = currentStack.getTop();
                ParserStackElement nextTop = currentTop.nextTk(token, node);
                if (nextTop != null) {
                    ParserStack nextStack = new ParserStack(null, nextTop, nextTop.getDepth());
                    stacksList.add(nextStack);
                }
            }
        }
        
        //Check a syntax error.
        if (stacksList.size() == 0) {
            TextPos pos = node.getPos();
            Collection<TokenDescriptor> expectedTokens = ParserEngineHelper.getExpectedTokens(prevStacks);
            throw new SynSyntaxException(pos, node, expectedTokens);
        }
    }
    
    /**
     * Checks if the specified stack is cycled. A stack is cycled if the same state was added twice to the
     * stack's state chain during one {@link #reduceCurrentStacks()} call. Such stack has to be rejected.
     */
    private static boolean isCycledStack(ParserStack stack) {
        int reduceStartDepth = stack.getReduceDepth();
        ParserStackElement top = stack.getTop();
        ParserState topState = top.getState();
        
        ParserStackElement element = top.getPrev();
        while (element.getDepth() > reduceStartDepth) {
            if (element.getState() == topState) {
                return true;
            }
            element = element.getPrev();
        }
        
        return false;
    }
    
    /**
     * Returns <code>true</code> if the stack is recursive, i. e. it was produced by a sequence of
     * reductions of an equal stack.
     */
    private static boolean isRecursiveStack(ParserStack stack) {
        ParserStackElement top = stack.getTop();
        
        ParserStack sourceStack = stack.getSourceStack();
        while (sourceStack != null) {
            if (top.equals(sourceStack.getTop())) {
                return true;
            }
            sourceStack = sourceStack.getSourceStack();
        }
        
        return false;
    }
}
