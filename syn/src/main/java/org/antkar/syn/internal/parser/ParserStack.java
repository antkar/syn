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
package org.antkar.syn.internal.parser;

/**
 * Parser stack. Contains a chain of LR states.
 */
class ParserStack {
    
    /** Top element of the stack. */
    private final ParserStackElement top;
    
    /**
     * The depth of the stack at the point when a reduction operation started. Allows to determine which
     * stack elements were added during a single reduction operation. Used by
     * {@link ParserEngine#isCycledStack(ParserStack)}.
     */
    private final int reduceDepth;
    
    /** Source stack - another stack from which this stack was obtained by a reduction
     * (<code>null</code>, if this is not a reduction stack). */
    private final ParserStack sourceStack;
    
    /** First element of the linked list of derived stacks - the stacks obtained by reducing this one. */
    private ParserStack derivedStack;

    /** Next element of the linked list of derived stacks that have the same source stack as this one. */
    private ParserStack derivedStackLink;
    
    /** <code>true</code> if this stack was deleted because of an ambiguity. Deleted stacks are not removed
     * from the stacks list for efficiency reasons. */
    private boolean deleted;

    ParserStack(ParserStack sourceStack, ParserStackElement top, int reduceDepth) {
        assert top != null;
        this.sourceStack = sourceStack;
        this.top = top;
        this.reduceDepth = reduceDepth;
        
        if (sourceStack != null) {
            //The source stack is specified - add this stack to the source's stack list of derived stacks.
            this.derivedStackLink = sourceStack.derivedStack;
            sourceStack.derivedStack = this;
        }
    }

    /**
     * Returns the source stack.
     */
    ParserStack getSourceStack() {
        return sourceStack;
    }

    /**
     * Returns the top element of the stack.
     */
    ParserStackElement getTop() {
        return top;
    }
    
    /**
     * Returns the reduction depth of this stack.
     */
    int getReduceDepth() {
        return reduceDepth;
    }
    
    /**
     * Marks this stack and all derived stacks as deleted.
     */
    void delete() {
        if (!deleted) {
            deleted = true;
            for (ParserStack stack = derivedStack; stack != null; stack = stack.derivedStackLink) {
                stack.delete();
            }
        }
    }
    
    /**
     * Returns <code>true</code> if the stack was deleted.
     */
    boolean isDeleted() {
        return deleted;
    }
    
    @Override
    public int hashCode() {
        int hashCode = top.hashCode();
        return hashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean result;
        if (obj instanceof ParserStack) {
            result = ((ParserStack) obj).top.equals(top);
        } else {
            result = super.equals(obj);
        }
        return result;
    }
    
    @Override
    public String toString() {
        String toString = top.toString();
        return toString;
    }
}
