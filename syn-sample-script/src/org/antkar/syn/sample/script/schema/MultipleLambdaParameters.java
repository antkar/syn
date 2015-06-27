package org.antkar.syn.sample.script.schema;

import java.util.Arrays;
import java.util.List;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;
import org.antkar.syn.TextPos;

/**
 * Lambda expression formal parameters in parentheses, e. g. (a, b, c) -> ? or () -> ?.
 */
public class MultipleLambdaParameters extends LambdaParameters {
    @SynField
    private TextPos synPos;
    
    @SynField
    private StringToken[] synNames;
    
    public MultipleLambdaParameters(){}

    @Override
    TextPos getStartPos() {
        return synPos;
    }

    @Override
    List<StringToken> getParameters() {
        return Arrays.asList(synNames);
    }
    
    @Override
    public String toString() {
        return Arrays.toString(synNames);
    }
}
