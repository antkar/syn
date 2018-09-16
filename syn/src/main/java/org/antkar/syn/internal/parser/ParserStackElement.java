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

import java.io.PrintStream;

import org.antkar.syn.TerminalNode;
import org.antkar.syn.TokenDescriptor;
import org.antkar.syn.internal.Checks;
import org.antkar.syn.internal.lrtables.ParserProduction;
import org.antkar.syn.internal.lrtables.ParserState;

/**
 * Parser stack element. Has an associated LR state.
 */
abstract class ParserStackElement {
    static final ParserStackElement[] EMPTY_ARRAY = new ParserStackElement[0];

    /** Associated LR state. */
    private final ParserState state;

    /** Next (deeper) element of the stack. */
    private final ParserStackElement prev;

    /** Depth - the size of the stack, including this element. */
    private final int depth;

    private final int hashCode;

    ParserStackElement(ParserStackElement prev, ParserState state) {
        this.prev = prev;
        this.state = Checks.notNull(state);

        if (prev != null) {
            hashCode = prev.hashCode * 31 + state.getIndex();
            depth = prev.depth + 1;
        } else {
            hashCode = state.getIndex();
            depth = 1;
        }
    }

    /**
     * Returns <code>true</code> if the state associated with this stack element is a final state for
     * the start nonterminal.
     */
    final boolean isCompleteTree() {
        boolean result = state.isFinal();
        return result;
    }

    /**
     * Returns the LR state associated with this element.
     */
    final ParserState getState() {
        return state;
    }

    /**
     * Returns the depth of the stack starting with this element.
     */
    final int getDepth() {
        return depth;
    }

    /**
     * Returns the stack element located at the specified offset relative to this element.
     */
    final ParserStackElement getDeep(int offset) {
        Checks.argument(offset >= 0);
        Checks.argument(offset < depth);

        ParserStackElement element = this;
        for (int i = 0; i < offset; ++i) {
            element = element.prev;
        }
        return element;
    }

    /**
     * Goes to the next state by a token, returns a new stack.
     */
    final ParserStackElement nextTk(TokenDescriptor token, TerminalNode node) {
        Checks.notNull(token);

        ParserState nextState = state.getShiftState(token);
        ParserStackElement result = null;
        if (nextState != null) {
            result = new TerminalParserStackElement(this, nextState, node);
        }

        return result;
    }

    /**
     * Goes to the next state by a nonterminal, returns a new stack.
     */
    final ParserStackElement nextNt(ParserProduction production, ParserStackElement subElements) {
        Checks.notNull(production);
        Checks.notNull(subElements);
        Checks.argument(subElements.depth > production.getLength());

        ParserState nextState = state.getGotoState(production.getNonterminal());
        ParserStackElement result = null;
        if (nextState != null) {
            result = new NonterminalParserStackElement(this, nextState, production, subElements);
        }

        return result;
    }

    /**
     * Returns the previous element.
     */
    final ParserStackElement getPrev() {
        return prev;
    }

    /**
     * Two stacks are equal if their chains of LR states are equal.
     */
    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof ParserStackElement)) {
            return false;
        }

        ParserStackElement el = (ParserStackElement) obj;
        if (el.depth != depth) {
            return false;
        }

        ParserStackElement el2 = this;
        while (el2 != el && el2 != null) {
            if (el2.hashCode != el.hashCode || el2.state != el.state) {
                return false;
            }
            el2 = el2.prev;
            el = el.prev;
        }

        return true;
    }

    @Override
    public final int hashCode() {
        return hashCode;
    }

    @Override
    public final String toString() {
        StringBuilder bld = new StringBuilder();

        ParserStackElement element = this;
        while (element != null) {
            if (bld.length() > 0) {
                bld.insert(0, " ");
            }
            bld.insert(0, element.state.getIndex());
            element = element.prev;
        }

        return bld.toString();
    }

    /**
     * Creates a parser node for this stack element and its sub-elements.
     */
    abstract IParserNode createParserNode();

    /**
     * Creates an ambiguity node for this stack element and its sub-elements.
     */
    abstract AmbiguityNode createAmbiguityNode();

    /**
     * Prints this stack element and its sub-elements. Used for debug purposes.
     */
    abstract void print(PrintStream out, int level);
}
