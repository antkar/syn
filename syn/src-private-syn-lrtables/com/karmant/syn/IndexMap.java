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

import java.util.Arrays;
import java.util.Map;

/**
 * An indexed map. Supports a limited set of keys. Each key has an unique <code>int</code> index, all indexes
 * are consequent and start from 0. Has a similar set of methods as {@link Map}, but does not implement
 * {@link Map} interface, since this class is used only locally.
 *
 * @param <K> the type of a key.
 * @param <V> the type of a value.
 */
class IndexMap<K, V> {
    
    private final IIndexProvider<K> indexProvider;
    
    /** Table translating an index into the position of an element. Value <code>-1</code> means that
     * the element is not in the map. */
    private final int[] indexToPos;

    /** Maps an element position to the index of the element. */
    private final int[] posToIndex;
    
    /** Maps an element position to the key of the element. */
    private final K[] posToKey;
    
    /** Maps an element position to the value of the element. */
    private final V[] posToValue;
    
    /** Maximum size - the maximum number of keys. */
    private final int maxSize;
    
    /** Current size. */
    private int size;

    /**
     * Constructs an indexed map.
     * 
     * @param indexProvider an index provider.
     * @param maxSize the maximum size, being the maximum key index plus one.
     */
    IndexMap(IIndexProvider<K> indexProvider, int maxSize) {
        assert indexProvider != null;
        assert maxSize > 0;
        
        this.indexProvider = indexProvider;
        this.maxSize = maxSize;
        
        indexToPos = new int[maxSize];
        posToIndex = new int[maxSize];
        posToKey = createTypedArray(maxSize);
        posToValue = createTypedArray(maxSize);
        size = 0;
        
        Arrays.fill(indexToPos, -1);
    }
    
    /**
     * Casts an array of {@link Object} to an array of an arbitrary type, hiding the compiler's warning.
     */
    @SuppressWarnings("unchecked")
    static <T> T[] createTypedArray(int size) {
        return (T[])new Object[size];
    }
    
    /**
     * Returns <code>true</code> if the passed key is in the map.
     */
    boolean containsKey(K key) {
        assert key != null;
        int index = indexProvider.getIndex(key);
        assert index < maxSize;
        int pos = indexToPos[index];
        return pos != -1;
    }
    
    /**
     * Puts a value into the map.
     */
    void put(K key, V value) {
        assert key != null;
        int index = indexProvider.getIndex(key);
        assert index < maxSize;
        if (indexToPos[index] == -1) {
            posToIndex[size] = index;
            posToKey[size] = key;
            posToValue[size] = value;
            indexToPos[index] = size;
            ++size;
        }
    }
    
    /**
     * Gets a value from the map.
     */
    V get(K key) {
        assert key != null;
        int index = indexProvider.getIndex(key);
        assert index < maxSize;
        int pos = indexToPos[index];
        V result = pos != -1 ? (V)posToValue[pos] : null;
        return result;
    }
    
    /**
     * Removes a value from the map.
     */
    void remove(K key) {
        assert key != null;
        int index = indexProvider.getIndex(key);
        assert index < maxSize;
        int pos = indexToPos[index];
        
        if (pos != -1) {
            if (pos < size - 1) {
                posToIndex[pos] = posToIndex[size - 1];
                posToKey[pos] = posToKey[size - 1];
                posToValue[pos] = posToValue[size - 1];
            }
            posToKey[size - 1] = null;
            posToValue[size - 1] = null;
            indexToPos[index] = -1;
            --size;
        }
    }
    
    /**
     * Clears the map.
     */
    void clear() {
        for (int pos = 0; pos < size; ++pos) {
            int index = posToIndex[pos];
            indexToPos[index] = -1;
            posToKey[pos] = null;
            posToValue[pos] = null;
        }
        size = 0;
    }
    
    /**
     * Returns the size of the map.
     */
    int size() {
        return size;
    }
    
    /**
     * Returns the key at the given position.
     */
    K getKeyAt(int pos) {
        if (pos < 0 || pos >= size) {
            throw new ArrayIndexOutOfBoundsException(pos);
        }
        return posToKey[pos];
    }

    /**
     * Returns the value at the given position.
     */
    V getValueAt(int pos) {
        if (pos < 0 || pos >= size) {
            throw new ArrayIndexOutOfBoundsException(pos);
        }
        return posToValue[pos];
    }
    
    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("{");
        
        String sep = "";
        for (int pos : indexToPos) {
            if (pos != -1) {
                bld.append(sep);
                bld.append(posToKey[pos]);
                bld.append("=");
                bld.append(posToValue[pos]);
                sep = ", ";
            }
        }
        
        bld.append("}");
        return bld.toString();
    }
}
