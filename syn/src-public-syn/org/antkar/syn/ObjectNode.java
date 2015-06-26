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

import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.antkar.syn.CommonUtil;

/**
 * Object node. Represents the result of a production with attributes. 
 * <p>
 * Implements {@link java.util.Map Map&lt;String,SynNode&gt;} for reasons of convenience.
 * Modification attempts cause {@link UnsupportedOperationException}.
 */
public class ObjectNode extends SynNode implements Map<String, SynNode> {

    /**
     * List of entries (key-value pairs). This solution seems optimal, since productions usually do not
     * have more than a few attributes.
     */
    private final List<ObjectEntry> entries;
    
    ObjectNode(List<ObjectEntry> entries) {
        assert entries != null;
        this.entries = entries;
    }
    
    /**
     * Returns the {@link TextPos text position} of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the text position of the node, or <code>null</code> if the node is <code>null</code>.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link TerminalNode}.
     */
    public TextPos getPos(String key) {
        TerminalNode tNode = (TerminalNode) get(key);
        TextPos pos = tNode == null ? null : tNode.getPos();
        return pos;
    }
    
    /**
     * Returns the {@link StringIndexOutOfBoundsException} value of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the result of the {@link TerminalNode#getString()} method invocation on the node,
     * or <code>null</code> if the node is <code>null</code>.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link TerminalNode}.
     */
    public String getString(String key) {
        TerminalNode tNode = (TerminalNode) get(key);
        String result = tNode == null ? null : tNode.getString();
        return result;
    }

    /**
     * Returns the <code>int</code> value of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the result of the {@link ValueNode#getInt()} method invocation on the node.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
     * @throws NullPointerException if the node is <code>null</code>.
     */
    public int getInt(String key) {
        ValueNode vNode = getValueNode(key);
        return vNode.getInt();
    }
    
    /**
     * Returns the <code>long</code> value of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the result of the {@link ValueNode#getLong()} method invocation on the node.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
     * @throws NullPointerException if the node is <code>null</code>.
     */
    public long getLong(String key) {
        ValueNode vNode = getValueNode(key);
        return vNode.getLong();
    }
    
    /**
     * Returns the boolean value of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the result of the {@link ValueNode#getBoolean()} method invocation on the node.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
     * @throws NullPointerException if the node is <code>null</code>.
     */
    public boolean getBoolean(String key) {
        ValueNode vNode = getValueNode(key);
        return vNode.getBoolean();
    }
    
    /**
     * Returns the floating-point value of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the result of the {@link ValueNode#getFloat()} method invocation on the node.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
     * @throws NullPointerException if the node is <code>null</code>.
     */
    public double getFloat(String key) {
        ValueNode vNode = getValueNode(key);
        return vNode.getFloat();
    }

    /**
     * Returns the {@link Object} value of the node with the specified key.
     * 
     * @param key the key of the node.
     * @return the result of the {@link ValueNode#getValue()} method invocation on the node.
     * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
     * @throws NullPointerException if the node is <code>null</code>.
     */
    public Object getValue(String key) {
        ValueNode vNode = getValueNode(key);
        return vNode.getValue();
    }

    @Override
    public boolean containsKey(Object key) {
        ObjectEntry entry = findEntry(key);
        return entry != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0, n = entries.size(); i < n; ++i) {
            SynNode node = entries.get(i).value;
            if (value == null ? node == null : value.equals(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Entry<String, SynNode>> entrySet() {
        return new ObjNodeEntrySet();
    }
    
    List<ObjectEntry> entryList() {
        return entries;
    }

    @Override
    public SynNode get(Object key) {
        ObjectEntry entry = findEntry(key);
        return entry == null ? null : entry.value;
    }
    
    private ObjectEntry findEntry(Object key) {
        for (int i = 0, n = entries.size(); i < n; ++i) {
            ObjectEntry entry = entries.get(i);
            if (entry.key.equals(key)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return new ObjNodeKeySet();
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Collection<SynNode> values() {
        return new ObjNodeValueCollection();
    }
    
    private ValueNode getValueNode(String key) {
        SynNode node = get(key);
        return (ValueNode) node;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("{");
        
        String sep = "";
        for (int i = 0, n = entries.size(); i < n; ++i) {
            ObjectEntry entry = entries.get(i);
            bld.append(sep);
            bld.append(entry.key);
            bld.append("=");
            bld.append(entry.value);
            sep = ", ";
        }
        
        bld.append("}");
        String result = bld.toString();
        return result;
    }

    @Override
    void print(PrintStream out, int level) {
        out.println("object");
        
        for (int i = 0, n = entries.size(); i < n; ++i) {
            ObjectEntry entry = entries.get(i);
            CommonUtil.printIndent(out, level + 1);
            out.print(entry.key + " = ");
            if (entry.value == null) {
                out.println(null + "");
            } else {
                entry.value.print(out, level + 2);
            }
        }
    }

    //Modifications are not allowed.
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SynNode put(String key, SynNode value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends SynNode> t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SynNode remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Map entry, a key-value pair.
     */
    static class ObjectEntry implements Entry<String, SynNode> {
        private final String key;
        private final SynNode value;
        
        ObjectEntry(String key, SynNode value) {
            assert key != null;
            this.key = key;
            this.value = value;
        }
        
        @Override
        public String getKey() {
            return key;
        }

        @Override
        public SynNode getValue() {
            return value;
        }

        @Override
        public SynNode setValue(SynNode value) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Map.Entry<?,?>) {
                Map.Entry<?,?> entry = (Map.Entry<?,?>)obj;
                if (!key.equals(entry.getKey())) {
                    return false;
                }
                Object entryValue = entry.getValue();
                return value == null ? entryValue == null : value.equals(entryValue);
            }
            
            return super.equals(obj);
        }
        
        @Override
        public int hashCode() {
            return key.hashCode() * 31 + (value == null ? 0 : value.hashCode());
        }
        
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    /**
     * Common superclass for a map component collection (the collection returned by {@link ObjectNode#entrySet()},
     * {@link ObjectNode#keySet()} or {@link ObjectNode#values()} method).
     * 
     * @param <E> the type of the component.
     */
    private abstract class ObjNodeInternalCollection<E> extends AbstractCollection<E> {
        ObjNodeInternalCollection(){}

        /**
         * Get the map component for the specified entry.
         */
        abstract E getComponent(ObjectEntry entry);
        
        //Used by iterator.
        final ObjectEntry getEntryAt(int index) {
            return entries.get(index);
        }
        
        @Override
        public final int size() {
            return entries.size();
        }

        @Override
        public final boolean isEmpty() {
            return entries.isEmpty();
        }
        
        @Override
        public final Iterator<E> iterator() {
            return new ObjNodeInternalIterator<>(this);
        }
        
        @Override
        public final boolean contains(Object o) {
            //Look for an entry which component is equal to the specified object.
            for (int i = 0, n = entries.size(); i < n; ++i) {
                ObjectEntry entry = entries.get(i);
                Object c = getComponent(entry);
                if (o == null ? c == null : o.equals(c)) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public final Object[] toArray() {
            Object[] array = new Object[entries.size()];
            copyToArray(array);
            return array;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final <T> T[] toArray(T[] a) {
            int size = entries.size();
            if (a.length < size) {
                a = (T[])Array.newInstance(a.getClass().getComponentType(), size);
            }
            copyToArray(a);
            return a;
        }
        
        /**
         * Copies elements of this collection into the specified array.
         */
        private void copyToArray(Object[] array) {
            for (int i = 0, n = entries.size(); i < n; ++i) {
                ObjectEntry entry = entries.get(i);
                array[i] = getComponent(entry);
            }
        }
        
        //Modifications are not allowed.
        
        @Override
        public final boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void clear() {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Value collection.
     */
    private final class ObjNodeValueCollection extends ObjNodeInternalCollection<SynNode> {
        ObjNodeValueCollection(){}
        
        @Override
        SynNode getComponent(ObjectEntry entry) {
            return entry.value;
        }
    }

    /**
     * Common superclass for a map component set (a key set or an entry set).
     * 
     * @param <E> the type of the component.
     */
    private abstract class ObjNodeInternalSet<E> extends ObjNodeInternalCollection<E> implements Set<E> {
        ObjNodeInternalSet(){}
    }

    /**
     * Key set.
     */
    private final class ObjNodeKeySet extends ObjNodeInternalSet<String> {
        ObjNodeKeySet(){}
        
        @Override
        String getComponent(ObjectEntry entry) {
            return entry.key;
        }
    }

    /**
     * Entry set.
     */
    private final class ObjNodeEntrySet extends ObjNodeInternalSet<Map.Entry<String, SynNode>> {
        ObjNodeEntrySet(){}

        @Override
        java.util.Map.Entry<String, SynNode> getComponent(ObjectEntry entry) {
            return entry;
        }
    }
    
    /**
     * Common superclass for a map component collection iterator.
     *
     * @param <E> the type of the component.
     */
    private static final class ObjNodeInternalIterator<E> implements Iterator<E> {
        private final ObjNodeInternalCollection<E> collection;
        private int pos;
        
        ObjNodeInternalIterator(ObjNodeInternalCollection<E> collection) {
            this.collection = collection;
        }
        
        @Override
        public boolean hasNext() {
            return pos < collection.size();
        }

        @Override
        public E next() {
            if (pos >= collection.size()) {
                throw new NoSuchElementException();
            }
            ObjectEntry entry = collection.getEntryAt(pos);
            ++pos;
            return collection.getComponent(entry);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
