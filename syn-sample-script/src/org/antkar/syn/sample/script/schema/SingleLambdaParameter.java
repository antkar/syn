package org.antkar.syn.sample.script.schema;

import java.util.Collections;
import java.util.List;

import org.antkar.syn.StringToken;
import org.antkar.syn.SynField;
import org.antkar.syn.TextPos;

/**
 * A single lambda expression formal parameter without parentheses: x -> ?.
 */
public class SingleLambdaParameter extends LambdaParameters {
    @SynField
    private StringToken synName;
    
    public SingleLambdaParameter(){}

    @Override
    TextPos getStartPos() {
        return synName.getPos();
    }

    @Override
    List<StringToken> getParameters() {
        return Collections.singletonList(synName);
    }
    
    @Override
    public String toString() {
        return "" + synName;
    }
}
