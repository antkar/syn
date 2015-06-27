package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.StringToken;
import org.antkar.syn.TextPos;

/**
 * Lambda expression formal parameters.
 */
public abstract class LambdaParameters {
    LambdaParameters(){}
    
    abstract TextPos getStartPos();
    
    abstract List<StringToken> getParameters();
}
