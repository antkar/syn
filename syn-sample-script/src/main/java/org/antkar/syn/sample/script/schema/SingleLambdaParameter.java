package org.antkar.syn.sample.script.schema;

import java.util.Collections;
import java.util.List;

import org.antkar.syn.TextPos;
import org.antkar.syn.binder.StringToken;
import org.antkar.syn.binder.SynField;

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
