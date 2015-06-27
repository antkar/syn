package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.StringToken;

public abstract class FunctionBody {
    FunctionBody(){}
    
    abstract FunctionObject createFunction(String scopeName, List<StringToken> parameters);
}
