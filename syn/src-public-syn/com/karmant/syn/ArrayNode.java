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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Array node. Represents the result of a repetition element.
 * <p>
 * Implements {@link java.util.List List&lt;SynNode&gt;} for reasons of convenience.
 * Modification attempts cause {@link UnsupportedOperationException}.
 */
/**
 * @author a.karmanov
 *
 */
public class ArrayNode extends SynNode implements List<SynNode> {
	private final List<SynNode> list;
	
	ArrayNode(List<SynNode> list) {
		assert list != null;
		this.list = list;
	}
	
	/**
	 * Returns the {@link TextPos text position} of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the text position of the node, or <code>null</code> if the node is <code>null</code>.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link TerminalNode}.
	 */
	public TextPos getPos(int index) {
		TerminalNode tNode = (TerminalNode) get(index);
		TextPos pos = tNode == null ? null : tNode.getPos();
		return pos;
	}
	
	/**
	 * Returns the {@link String} value of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the result of the {@link TerminalNode#getString()} method invocation on the node,
	 * or <code>null</code> if the node is <code>null</code>.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link TerminalNode}.
	 */
	public String getString(int index) {
		TerminalNode tNode = (TerminalNode) get(index);
		String result = tNode == null ? null : tNode.getString();
		return result;
	}

	/**
	 * Returns the <code>int</code> value of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the result of the {@link ValueNode#getInt()} method invocation on the node.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
	 * @throws NullPointerException if the node is <code>null</code>.
	 */
	public int getInt(int index) {
		ValueNode vNode = getValueNode(index);
		return vNode.getInt();
	}
	
	/**
	 * Returns the <code>long</code> value of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the result of the {@link ValueNode#getLong()} method invocation on the node.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
	 * @throws NullPointerException if the node is <code>null</code>.
	 */
	public long getLong(int index) {
		ValueNode vNode = getValueNode(index);
		return vNode.getLong();
	}
	
	/**
	 * Returns the boolean value of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the result of the {@link ValueNode#getBoolean()} method invocation on the node.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
	 * @throws NullPointerException if the node is <code>null</code>.
	 */
	public boolean getBoolean(int index) {
		ValueNode vNode = getValueNode(index);
		return vNode.getBoolean();
	}
	
	/**
	 * Returns the floating-point value of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the result of the {@link ValueNode#getFloat()} method invocation on the node.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
	 * @throws NullPointerException if the node is <code>null</code>.
	 */
	public double getFloat(int index) {
		ValueNode vNode = getValueNode(index);
		return vNode.getFloat();
	}

	/**
	 * Returns the {@link Object} value of the node at the specified index.
	 * 
	 * @param index the index of the node.
	 * @return the result of the {@link ValueNode#getValue()} method invocation on the node.
	 * @throws ClassCastException if the node is not <code>null</code> and not a {@link ValueNode}.
	 * @throws NullPointerException if the node is <code>null</code>.
	 */
	public Object getValue(int index) {
		ValueNode vNode = getValueNode(index);
		return vNode.getValue();
	}

	/**
	 * Casts the node at the specified index to a {@link ValueNode}.
	 */
	private ValueNode getValueNode(int index) {
		SynNode node = get(index);
		return (ValueNode) node;
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public SynNode get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<SynNode> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<SynNode> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<SynNode> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<SynNode> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public String toString() {
		return list.toString();
	}
	
	@Override
	public void print(PrintStream out, int level) {
		out.println("array[" + size() + "]");
		
		for (int i = 0, n = size(); i < n; ++i) {
			CommonUtil.printIndent(out, level + 1);
			out.print("[" + i + "] = ");
			
			SynNode subNode = get(i);
			if (subNode == null) {
				out.println(null + "");
			} else {
				subNode.print(out, level + 2);
			}
		}
	}
	
	//Modifications are not allowed.
	
	@Override
	public void add(int index, SynNode element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(SynNode o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends SynNode> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends SynNode> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SynNode remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SynNode set(int index, SynNode element) {
		throw new UnsupportedOperationException();
	}
}
