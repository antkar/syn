package org.antkar.syn.sample.script.schema;

import java.util.List;

import org.antkar.syn.binder.StringToken;
import org.antkar.syn.sample.script.rt.ScriptScope;
import org.antkar.syn.sample.script.rt.StatementResult;
import org.antkar.syn.sample.script.rt.SynsException;
import org.antkar.syn.sample.script.rt.value.Value;

/**
 * Function object implemented by a block (used in standard-form functions).
 */
class BlockFunctionObject extends FunctionObject {
    private final Block block;

    private BlockFunctionObject(String scopeName, List<StringToken> parameters, Block block) {
        super(scopeName, parameters);
        this.block = block;
    }

    @Override
    Value execute(ScriptScope scope) throws SynsException {
        StatementResult result = block.execute(scope);
        return result.isReturn() ? result.getReturnValue() : Value.forVoid();
    }

    static final class Factory implements FunctionObject.Factory {
        private final Block block;

        Factory(Block block) {
            this.block = block;
        }

        @Override
        public FunctionObject create(String scopeName, List<StringToken> parameters) {
            return new BlockFunctionObject(scopeName, parameters, block);
        }
    }
}
