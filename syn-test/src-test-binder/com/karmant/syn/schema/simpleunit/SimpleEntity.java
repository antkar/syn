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
package com.karmant.syn.schema.simpleunit;

import com.karmant.syn.SynField;
import com.karmant.syn.SynInit;
import com.karmant.syn.SynLookup;

public class SimpleEntity {
	@SynField
	private String sfName;
	
	@SynField
	private SimpleMember[] sfMembers;
	
	@SynLookup("obj != this && obj.sfName == this.sfName")
	private SimpleEntity[] entitiesWithSameName;
	
	public SimpleEntity() {
		super();
	}
	
	public String getName() {
		return sfName;
	}
	
	public SimpleMember[] getMembers() {
		return sfMembers;
	}
	
	@SynInit
	private void init() {
		if (entitiesWithSameName.length > 0) {
			throw new IllegalStateException(String.format("There is more than one entity with name %s", sfName));
		}
	}
}
