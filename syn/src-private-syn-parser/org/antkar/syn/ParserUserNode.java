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

import org.antkar.syn.SynNode;

/**
 * User parser node. Wraps a {@link SynNode}, which will be returned to the client.
 */
class ParserUserNode implements IParserNode {
    private final SynNode userNode;
    
    ParserUserNode(SynNode userNode) {
        this.userNode = userNode;
    }

    @Override
    public SynNode createUserNode() {
        return userNode;
    }
    
    @Override
    public String toString() {
        return userNode + "";
    }
}
