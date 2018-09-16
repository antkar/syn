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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antkar.syn.internal.Checks;
import org.antkar.syn.internal.lrtables.ParserState;

/**
 * List of parser stacks.
 */
final class StacksList {

    /** Primary list of stacks. */
    private List<ParserStack> list;

    /** Back, or secondary list of stacks. */
    private List<ParserStack> backList;

    /** Map of stacks. Used to detect conflicting stacks. */
    private final Map<ParserStackElement, ParserStack> map;

    /** Result stack - a stack which associated LR state is final. */
    private ParserStack resultStack;

    StacksList() {
        list = new ArrayList<>();
        backList = new ArrayList<>();
        map = new HashMap<>();
    }

    /**
     * Returns the number of stacks in the list.
     */
    int size() {
        return list.size();
    }

    /**
     * Returns current list of stacks and switches to a new empty one, which becomes current.
     */
    List<ParserStack> copyAndClear() {
        map.clear();

        List<ParserStack> temp = list;
        list = backList;
        backList = temp;
        list.clear();
        resultStack = null;

        return backList;
    }

    /**
     * Returns a stack at the given position in the list.
     */
    ParserStack getByPos(int index) {
        return list.get(index);
    }

    /**
     * Returns a stack equal to the specified one.
     */
    ParserStack getByTop(ParserStackElement top) {
        return map.get(top);
    }

    /**
     * Adds a stack to the list.
     */
    void add(ParserStack stack) {
        list.add(stack);
        ParserStackElement top = stack.getTop();
        map.put(stack.getTop(), stack);

        updateResultStack(stack, top);
    }

    /**
     * Replaces an existing stack by the specified one.
     */
    void replace(ParserStack stack) {
        list.add(stack);

        ParserStackElement top = stack.getTop();
        map.put(top, stack);

        updateResultStack(stack, top);
    }

    /**
     * If the specified stack is a result stack, it is remembered.
     */
    private void updateResultStack(ParserStack stack, ParserStackElement top) {
        ParserState state = top.getState();
        if (state.isFinal()) {
            Checks.state(resultStack == null || resultStack.isDeleted());
            resultStack = stack;
        }
    }

    /**
     * Returns the result stack, if one is currently in the list.
     */
    ParserStack getResultStack() {
        return resultStack;
    }
}
