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
package org.antkar.syn.internal.lrtables;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.antkar.syn.internal.Checks;

/**
 * An indexed set. Similar to an {@link IndexMap}.
 *
 * @param <T> the type of an element.
 */
final class IndexSet<T> {

    private final IIndexProvider<T> indexProvider;

    private final int maxSize;
    private final int[] indexToPos;
    private final int[] posToIndex;
    private final T[] posToValue;
    private int size;

    /**
     * Constructs an indexed set.
     *
     * @param indexProvider an index provider.
     * @param maxSize the maximum size of the set, being the maximum element index plus one.
     */
    IndexSet(IIndexProvider<T> indexProvider, int maxSize) {
        Checks.argument(maxSize >= 0);

        this.indexProvider = Checks.notNull(indexProvider);
        indexToPos = new int[maxSize];
        posToIndex = new int[maxSize];
        posToValue = IndexMap.createTypedArray(maxSize);
        this.maxSize = maxSize;
        size = 0;

        Arrays.fill(indexToPos, -1);
    }

    /**
     * Returns <code>true</code> if the set contains the specified value.
     */
    boolean contains(T t) {
        Checks.notNull(t);
        int index = indexProvider.getIndex(t);
        Checks.state(index < maxSize);
        int pos = indexToPos[index];
        return pos != -1;
    }

    /**
     * Adds the specified value to the set.
     */
    void add(T t) {
        Checks.notNull(t);
        int index = indexProvider.getIndex(t);
        Checks.state(index < maxSize);

        if (indexToPos[index] == -1) {
            posToIndex[size] = index;
            posToValue[size] = t;
            indexToPos[index] = size;
            ++size;
        }
    }

    /**
     * Removes the specified value from the set.
     */
    void remove(T t) {
        Checks.notNull(t);
        int index = indexProvider.getIndex(t);
        Checks.state(index < maxSize);
        int pos = indexToPos[index];

        if (pos != -1) {
            if (pos < size - 1) {
                posToIndex[pos] = posToIndex[size - 1];
                posToValue[pos] = posToValue[size - 1];
            }
            posToValue[size - 1] = null;
            indexToPos[index] = -1;
            --size;
        }
    }

    /**
     * Clears the set.
     */
    void clear() {
        for (int pos = 0; pos < size; ++pos) {
            int index = posToIndex[pos];
            posToValue[pos] = null;
            indexToPos[index] = -1;
        }
        size = 0;
    }

    /**
     * Returns the size of the set.
     */
    int size() {
        return size;
    }

    /**
     * Returns the value at the given position.
     */
    T getAt(int pos) {
        if (pos < 0 || pos >= size) {
            throw new ArrayIndexOutOfBoundsException(pos);
        }
        T result = posToValue[pos];
        return result;
    }

    /**
     * Creates a typed array of elements.
     */
    @SuppressWarnings("unchecked")
    T[] asArray(Class<? extends T> clazz) {
        T[] array = (T[]) Array.newInstance(clazz, size);
        System.arraycopy(posToValue, 0, array, 0, size);
        return array;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("{");

        String sep = "";
        for (int pos : indexToPos) {
            if (pos != -1) {
                bld.append(sep);
                bld.append(posToValue[pos]);
                sep = ", ";
            }
        }

        bld.append("}");
        return bld.toString();
    }
}
