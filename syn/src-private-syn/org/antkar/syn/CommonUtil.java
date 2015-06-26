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
package org.antkar.syn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antkar.syn.FileSourceDescriptor;
import org.antkar.syn.SourceDescriptor;
import org.antkar.syn.StringSourceDescriptor;

/**
 * Utility methods.
 */
final class CommonUtil {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    
    private CommonUtil() {}
    
    /**
     * Creates a reader for reading the specified file.
     * 
     * @param file the file.
     * @return the reader.
     * @throws IOException if file open operation fails.
     */
    static Reader openFileReader(File file) throws IOException {
        InputStream in = new FileInputStream(file);
        try {
            Reader reader = new InputStreamReader(in, CHARSET);
            in = null;
            return reader;
        } finally {
            if (in != null) {
                //Close the input stream only if Reader construction failed.
                in.close();
            }
        }
    }
    
    /**
     * Creates a reader for reading the specified class loader resource.
     * 
     * @param resourceOrigin the class which the specified resource path is relative to.
     * @param resourcePath the resource path, relative to the specified reference class.  
     * @return the reader.
     * @throws IOException if the specified resource is not found, or another I/O error occurs.
     */
    static Reader openResourceReader(Class<?> resourceOrigin, String resourcePath) throws IOException {
        InputStream in = resourceOrigin.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new FileNotFoundException("File not found: " + resourcePath);
        }
        try {
            Reader reader = new InputStreamReader(in, CHARSET);
            in = null;
            return reader;
        } finally {
            if (in != null) {
                //Close the input stream only if Reader construction failed.
                in.close();
            }
        }
    }
    
    /**
     * Returns either the passed source descriptor, or a new one, if the former is <code>null</code>.
     * The new descriptor is created from a {@link File}.
     * 
     * @param file the file.
     * @param descriptor the descriptor.
     * @return the non-<code>null</code> descriptor.
     */
    static SourceDescriptor getSourceDescriptor(File file, SourceDescriptor descriptor) {
        return descriptor != null ? descriptor : new FileSourceDescriptor(file);
    }
    
    /**
     * Returns either the passed source descriptor, or a new one, if the former is <code>null</code>.
     * The new descriptor is created from a {@link String}.
     * 
     * @param path the string.
     * @param descriptor the descriptor.
     * @return the non-<code>null</code> descriptor.
     */
    static SourceDescriptor getSourceDescriptor(String path, SourceDescriptor descriptor) {
        return descriptor != null ? descriptor : new StringSourceDescriptor(path);
    }
    
    /**
     * Prints an indentation of the specified size. Used to output a tree in a text format.
     * 
     * @param out the stream where to print the indentation to.
     * @param level the indentation level. Must not be negative.
     */
    static void printIndent(PrintStream out, int level) {
        for (int i = 0; i < level; ++i) {
            out.print("  ");
        }
    }
    
    /**
     * Adds an element to a map of {@link Collection}s. If there is no collection in the map with the specified key,
     * a new {@link ArrayList} is put into the map.
     * 
     * @param map the map.
     * @param key the key for looking up a collection.
     * @param value the value to be added to the collection.
     */
    static <K, V> void addToCollectionMap(Map<K, Collection<V>> map, K key, V value) {
        Collection<V> collection = map.get(key);
        if (collection == null) {
            collection = new ArrayList<>();
            map.put(key, collection);
        }
        collection.add(value);
    }
    
    /**
     * Adds an element to a map of {@link List}s. If there is no list in the map with the specified key,
     * a new one is put into the map.
     * 
     * @param map the map.
     * @param key the key for looking up a list.
     * @param value the value to be added to the list.
     */
    static <K, V> void addToListMap(Map<K, List<V>> map, K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        list.add(value);
    }
    
    /**
     * Adds an element to a map of {@link Set}s. If there is no set in the map with the specified key,
     * a new one is put into the map.
     * 
     * @param map the map.
     * @param key the key for looking up a set.
     * @param value the value to be added to the set.
     */
    static <K, V> void addToSetMap(Map<K, Set<V>> map, K key, V value) {
        Set<V> set = map.get(key);
        if (set == null) {
            set = new HashSet<>();
            map.put(key, set);
        }
        set.add(value);
    }
    
    /**
     * Puts a value into a map of maps. If there is no map in the map of maps with the specified key,
     * a new {@link HashMap} is put into the map.
     * 
     * @param map1 the map of maps.
     * @param key1 the key for looking up a nested map.
     * @param key2 the key for putting the value into the nested map.
     * @param value the value.
     */
    static <K1, K2, V> void putToMapMap(Map<K1, Map<K2, V>> map1, K1 key1, K2 key2, V value) {
        Map<K2, V> map2 = map1.get(key1);
        if (map2 == null) {
            map2 = new HashMap<>();
            map1.put(key1, map2);
        }
        map2.put(key2, value);
    }
    
    /**
     * Gets a value from a map of maps.
     * 
     * @param map1 the map of maps.
     * @param key1 the key for the map of maps.
     * @param key2 the key for a nested map.
     * @return the value, or <code>null</code> if the key is not found neither in the outer,
     * nor in the nested map.
     */
    static <K1, K2, V> V getFromMapMap(Map<K1, Map<K2, V>> map1, K1 key1, K2 key2) {
        Map<K2, V> map2 = map1.get(key1);
        return map2 == null ? null : map2.get(key2);
    }
}
