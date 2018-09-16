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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX XML handler used by the Grammar of Grammar parser ({@link XmlGrammarParser}).
 * Builds a tree of {@link XmlNode}s.
 */
final class XmlHandler extends DefaultHandler {

    private XmlNode rootNode;
    private XmlNode currentNode = null;

    XmlHandler(){}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        Map<String, String> attrMap = new HashMap<>();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            String name = attributes.getQName(i);
            String value = attributes.getValue(i);
            attrMap.put(name, value);
        }

        XmlNode node = new XmlNode(currentNode, qName, attrMap);
        if (currentNode != null) {
            currentNode.addNestedNode(node);
        } else if (rootNode == null) {
            rootNode = node;
        }
        currentNode = node;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        currentNode = currentNode.getParentNode();
    }

    XmlNode getRootNode() {
        return rootNode;
    }
}
