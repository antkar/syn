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

/**
 * Nonterminal bound type. Binds a result of a nonterminal.
 */
class NonterminalBoundType extends AbstractBoundType {
	NonterminalBoundType(Class<?> cls) {
		super(cls);
	}

	@Override
	Object convertNode(BinderEngine<?> engine, SynNode synNode, BoundObject bObjOwner, String key)
			throws SynBinderException
	{
		if (synNode == null) {
			return null;
		}

		ObjectNode objectNode = (ObjectNode) synNode;
		BoundObject bObj = engine.bindObjectNode(objectNode);
		bObj.setOwner(bObjOwner);
		bObjOwner.addReference(key, bObj);
		
		Object obj = bObj.getJavaObject();
		return obj;
	}
}
