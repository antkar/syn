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

import java.util.Arrays;
import java.util.Comparator;

/**
 * Set of LR items.
 */
class ParserItemSet {
    /** Sorted array of items. */
    private final ParserItem[] items;

    ParserItemSet(ParserItem[] items) {
        this.items = items.clone();
        Arrays.sort(items, ITEM_COMPARATOR);
    }
    
    ParserItem[] getItems() {
        return items;
    }
    
    @Override
    public int hashCode() {
        int result = 0;
        for (ParserItem item : items) {
            result = result * 31 + item.getIndex();
        }
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ParserItemSet) {
            ParserItemSet iSet = (ParserItemSet) obj;
            if (iSet.items.length == items.length) {
                for (int i = 0, n = items.length; i < n; ++i) {
                    if (items[i].getIndex() != iSet.items[i].getIndex()) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        return Arrays.toString(items);
    }
    
    private static final Comparator<ParserItem> ITEM_COMPARATOR = new Comparator<ParserItem>() {
        @Override
        public int compare(ParserItem o1, ParserItem o2) {
            return o1.getIndex() - o2.getIndex();
        }
    };
}
