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
package com.karmant.syn.sample.script.ide;

/**
 * Dynamically resizable byte buffer. Bytes can be inserted into the buffer; the buffer is resized
 * automatically, if necessary.
 */
class ByteBuffer {
	private byte[] array;
	private int size;
	
	ByteBuffer() {
		array = new byte[16];
		size = 0;
	}
	
	/**
	 * Returns the byte at the given offset.
	 */
	int get(int offset) {
		return array[offset];
	}
	
	/**
	 * Sets the byte value at the given offset. The offset must be less than the current buffer size.
	 */
	void set(int offset, int value) {
		array[offset] = (byte)value;
	}
	
	/**
	 * Sets a range of bytes to the specified value. The range must not exceed the current buffer size.
	 */
	void set(int start, int end, int value) {
		byte bvalue = (byte)value;
		while (start < end) {
			array[start++] = bvalue;
		}
	}
	
	/**
	 * Inserts the specified value into the specified range of bytes. Existing bytes are moved
	 * forward. The buffer is resized, if necessary.
	 */
	void insert(int start, int end, int value) {
		int count = end - start;
		reserve(size + count);

		int left = size - start;
		if (left > 0) {
			System.arraycopy(array, start, array, end, left);
		}
		
		byte bvalue = (byte)value;
		for (int i = start; i < end; ++i) {
			array[i] = bvalue;
		}
		
		size += count;
	}
	
	/**
	 * Removes the specified range of bytes.
	 */
	void remove(int start, int end) {
		if (end < size) {
			System.arraycopy(array, end, array, start, size - end);
		}
		size -= end - start;
	}
	
	/**
	 * Clears the buffer.
	 */
	void clear() {
		size = 0;
	}
	
	/**
	 * Returns the size of the buffer.
	 */
	int size() {
		return size;
	}
	
	/**
	 * Reserves the specified number of bytes in the buffer. This method guarantees that at least
	 * the specified number of bytes is allocated for the buffer.
	 */
	private void reserve(int maxSize) {
		int length = array.length;
		if (length < maxSize) {
			int newLength = (length + length + length) >>> 2;
			if (newLength < maxSize) {
				newLength = maxSize;
			}
			byte[] newArray = new byte[newLength];
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}
}
