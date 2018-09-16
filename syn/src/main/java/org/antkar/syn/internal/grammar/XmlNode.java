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
package org.antkar.syn.internal.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A lightweight XML document node. Represents an XML element.
 */
class XmlNode {
    private final XmlNode parentNode;
    private List<XmlNode> nestedNodes = null;
    private final String name;
    private final Map<String, String> attributes;

    XmlNode(XmlNode parentNode, String name, Map<String, String> attributes) {
        assert attributes != null;
        assert name != null;
        
        this.parentNode = parentNode;
        this.name = name;
        this.attributes = Collections.unmodifiableMap(attributes);
    }
    
    /**
     * Returns the parent node, if any.
     */
    XmlNode getParentNode() {
        return parentNode;
    }

    /**
     * Adds a nested node to this node.
     */
    void addNestedNode(XmlNode node) {
        if (nestedNodes == null) {
            nestedNodes = new ArrayList<>();
        }
        nestedNodes.add(node);
    }
    
    /**
     * Returns the list of nested nodes.
     */
    List<XmlNode> getNestedNodes() {
        List<XmlNode> result = Collections.emptyList();
        if (nestedNodes != null) {
            result = nestedNodes;
        }
        return result;
    }
    
    /**
     * Returns the map of XML attributes.
     */
    Map<String, String> getAttributes() {
        return attributes;
    }
    
    /**
     * Returns the name of this node.
     */
    String getName() {
        return name;
    }
}
