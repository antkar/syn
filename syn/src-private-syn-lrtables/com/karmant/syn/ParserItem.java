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
 * LR item. Denotes a point in a BNF production.
 */
class ParserItem {
    private final int index;
    private final int pos;
    private final ParserItem next;
    private final ParserProduction production;
    private final BnfElement element;
    
    ParserItem(int index, int pos, ParserItem next, ParserProduction production, BnfElement element) {
        assert index >= 0;
        assert production != null;
        assert pos >= 0;
        assert pos <= production.getLength();
        
        this.index = index;
        this.pos = pos;
        this.next = next;
        this.production = production;
        this.element = element;
    }
    
    /**
     * Returns the index of this item within the grammar.
     */
    int getIndex() {
        return index;
    }
    
    /**
     * Returns the position of this item in its production.
     */
    int getPos() {
        return pos;
    }

    /**
     * Returns the next item in this production, or <code>null</code> if this one is the last item.
     */
    ParserItem getNext() {
        return next;
    }
    
    /**
     * Returns the parser production which this item belongs to.
     */
    ParserProduction getProduction() {
        return production;
    }
    
    /**
     * Returns the BNF element which this item points to, or <code>null</code> if this is the last item
     * in the production.
     */
    BnfElement getElement() {
        return element;
    }
    
    @Override
    public String toString() {
        return "[" + production.getNonterminal() + ": " + production + ", " + pos + "]";
    }
}
