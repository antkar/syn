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

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.antkar.syn.TokenDescriptor;

/**
 * Parser state. Contains an LR state.
 */
class ParserState {
    
    private final int index;
    private final List<ParserProduction> reduceProductions;
    private final boolean isFinal;
    private List<ParserShift> shifts = null;
    private List<ParserGoto> gotos = null;
    
    ParserState(int index, List<ParserProduction> reduceProductions) {
        assert index >= 0;
        assert reduceProductions != null;
        
        this.index = index;
        this.reduceProductions = Collections.unmodifiableList(reduceProductions);
        isFinal = calcIsFinal(reduceProductions);
    }
    
    void setTransitions(List<ParserShift> shifts, List<ParserGoto> gotos) {
        assert this.shifts == null;
        assert this.gotos == null;
        assert shifts != null;
        assert gotos != null;
        
        this.shifts = Collections.unmodifiableList(shifts);
        this.gotos = Collections.unmodifiableList(gotos);
    }

    /**
     * Returns the index of this state.
     */
    int getIndex() {
        return index;
    }
    
    /**
     * Returns the list of productions that can be reduced in this state.
     */
    List<ParserProduction> getReduceProductions() {
        assert shifts != null && gotos != null;
        return reduceProductions;
    };
    
    /**
     * Returns <code>true</code> if this is a final state which accepts an extended start nonterminal.
     */
    boolean isFinal() {
        return isFinal;
    }

    /**
     * Returns the LR state reachable from this one by a SHIFT transition.
     */
    ParserState getShiftState(TokenDescriptor tokenDescriptor) {
        assert shifts != null && gotos != null;
        
        ParserState result = null;
        for (int i = 0, n = shifts.size(); i < n; ++i) {
            ParserShift shift = shifts.get(i);
            if (shift.tokenDescriptor == tokenDescriptor) {
                result = shift.state;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Returns the LR state reachable from this one by a GOTO transition.
     */
    ParserState getGotoState(ParserNonterminal nonterminal) {
        assert shifts != null && gotos != null;
        
        ParserState result = null;
        for (int i = 0, n = gotos.size(); i < n; ++i) {
            ParserGoto got = gotos.get(i);
            if (got.nonterminal == nonterminal) {
                result = got.state;
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Returns the list of SHIFTs.
     */
    List<ParserShift> getShifts() {
        assert shifts != null;
        return shifts;
    }

    /**
     * Returns the list of GOTOs.
     */
    List<ParserGoto> getGotoList() {
        assert gotos != null;
        return gotos;
    }
    
    /**
     * Prints this state information into a stream. Used for debug purposes.
     */
    void print(PrintStream out) {
        out.println("---" + index + "---");
        for (ParserProduction production : reduceProductions) {
            out.println("reduce " + production.getNonterminal().getName() + " " + production.getLength());
        }
        for (ParserShift shift : shifts) {
            out.println("shift " + shift.tokenDescriptor + " " + shift.state.index);
        }
        for (ParserGoto pGoto : gotos) {
            out.println("goto " + pGoto.nonterminal.getName() + " " + pGoto.state.index);
        }
    }
    
    @Override
    public String toString() {
        return index + "";
    }
    
    /**
     * Determines whether there is a final production among the passed productions.
     */
    private static boolean calcIsFinal(List<ParserProduction> reduceProductions) {
        boolean result = false;
        for (ParserProduction production : reduceProductions) {
            ParserNonterminal nonterminal = production.getNonterminal();
            if (nonterminal.isExtended()) {
                result = true;
                break;
            }
        }
        return result;
    }
}
