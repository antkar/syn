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

import java.io.File;

/**
 * Source descriptor representing a {@link java.io.File}.
 */
public class FileSourceDescriptor implements SourceDescriptor {
	private final File file;
	
	public FileSourceDescriptor(File file) {
		if (file == null) {
			throw new NullPointerException("file");
		}
		this.file = file;
	}

	@Override
	public String getName() {
		return file.getPath();
	}
	
	public File getFile() {
		return file;
	}
	
	@Override
	public String toString() {
		return file.toString();
	}
}
