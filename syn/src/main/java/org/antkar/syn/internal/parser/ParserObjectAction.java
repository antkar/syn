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
package org.antkar.syn.internal.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antkar.syn.ObjectNode;
import org.antkar.syn.ObjectNode.ObjectEntry;
import org.antkar.syn.SynNode;

/**
 * Object parser action. Produces an {@link ObjectNode}.
 */
public final class ParserObjectAction implements IParserAction {

    /** Null object. Contains no attributes. */
    public static final ParserObjectAction NULL = new ParserObjectAction(
            new HashMap<String, IParserGetter>(),
            new ArrayList<IParserGetter>());

    /**
     * Fields. A list of name-value pairs is used, since there must usually be less than 5 attributes
     * in one grammar production. Using a Map would be inefficient.
     */
    private final List<ObjectField> fields;

    /**
     * Constructs an object action.
     *
     * @param getterMap a map of attribute getters, used to get values of attributes defined
     * directly in the production which this action is associated with.
     * @param embeddedGetters a collection of embedded objects getters, used to get embedded objects
     * (attributes are defined in embedded productions).
     */
    public ParserObjectAction(Map<String, IParserGetter> getterMap, Collection<IParserGetter> embeddedGetters) {
        assert getterMap != null;
        assert embeddedGetters != null;

        fields = createFields(getterMap, embeddedGetters);
    }

    /**
     * Creates the list of object fields.
     */
    private static List<ObjectField> createFields(
            Map<String, IParserGetter> getterMap,
            Collection<IParserGetter> embeddedGetters)
    {
        List<ObjectField> fields = new ArrayList<>(getterMap.size() + embeddedGetters.size());

        //Direct fields.
        for (Map.Entry<String, IParserGetter> entry : getterMap.entrySet()) {
            String key = entry.getKey();
            IParserGetter getter = entry.getValue();
            fields.add(new ObjectField(key, getter));
        }

        //Embedded fields.
        for (IParserGetter getter : embeddedGetters) {
            fields.add(new ObjectField(null, getter));
        }

        //Fields must be sorted by their offset, since a parser stack is passed in a form of a linked list,
        //so there is no random access to stack elements.
        Collections.sort(fields, FIELD_COMPARATOR);
        fields = Collections.unmodifiableList(fields);
        return fields;
    }

    @Override
    public IParserNode execute(ParserStackElement stack) {
        List<ObjectEntry> entries = new ArrayList<>();

        //Go through all fields and corresponding stack elements.
        int currentOfs = 0;
        for (int i = 0, n = fields.size(); i < n; ++i) {
            ObjectField field = fields.get(i);

            //Shift to the corresponding stack element.
            int fieldOfs = field.offset;
            while (currentOfs < fieldOfs) {
                stack = stack.getPrev();
                ++currentOfs;
            }

            //Initialize the field.
            getField(field, stack, entries);
        }

        SynNode userResult = new ObjectNode(entries);
        IParserNode result = new ParserUserNode(userResult);
        return result;
    }

    /**
     * Gets the field value from a stack and adds to an entry list.
     */
    private void getField(ObjectField field, ParserStackElement element, List<ObjectEntry> entries) {
        IParserNode value0 = field.getter.get(element);
        SynNode value = value0 == null ? null : value0.createUserNode();

        if (field.key != null) {
            //A key is specified - a direct attribute.
            entries.add(new ObjectEntry(field.key, value));
        } else {
            //Embedded object.
            getEmbeddedField(value, entries);
        }
    }

    /**
     * Adds entries from embedded objects.
     */
    private void getEmbeddedField(SynNode value, List<ObjectEntry> entries) {
        assert value != null;
        ObjectNode objectValue = (ObjectNode) value;

        List<ObjectEntry> embeddedEntries = objectValue.entryList();
        for (ObjectEntry entry : embeddedEntries) {
            String key = entry.getKey();
            if (!containsKey(entries, key)) {
                //Do not hide direct fields by embedded ones.
                entries.add(entry);
            }
        }
    }

    private static boolean containsKey(List<ObjectEntry> entries, String key) {
        for (int i = 0, n = entries.size(); i < n; ++i) {
            if (entries.get(i).getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        String sep = "";

        for (ObjectField field : fields) {
            bld.append(sep);
            if (field.key != null) {
                bld.append(field.key);
                bld.append(" = $");
                bld.append(field.offset);
            } else {
                bld.append("($");
                bld.append(field.offset);
                bld.append(")");
            }
            sep = ", ";
        }
        return "$$ = (" + fields + ")";
    }

    private static final Comparator<ObjectField> FIELD_COMPARATOR = new Comparator<ObjectField>() {
        @Override
        public int compare(ObjectField o1, ObjectField o2) {
            return Integer.compare(o1.offset, o2.offset);
        }
    };

    /**
     * Object field definition.
     */
    private static final class ObjectField {
        private final String key;
        private final IParserGetter getter;
        private final int offset;

        private ObjectField(String key, IParserGetter getter) {
            super();
            this.key = key;
            this.getter = getter;
            this.offset = getter.offset();
        }
    }
}
