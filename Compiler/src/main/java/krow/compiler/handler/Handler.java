package krow.compiler.handler;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.pre.PreClass;

public interface Handler {
    
    default boolean accepts(final String statement) {
        return false;
    }
    
    default boolean accepts(final String statement, final CompileContext context) {
        return accepts(statement);
    }
    
    default HandleResult handle(final String statement, final PreClass data, final CompileContext context) {
        return new HandleResult(null, statement, CompileState.ROOT);
    }
    
    default HandleResult handle(final String statement, final PreClass data, final CompileContext context, final CompileState state) {
        return handle(statement, data, context);
    }
    
    default HandleResult handle0(final String statement, final PreClass data, final CompileContext context, final CompileState state) {
        final HandleResult result = this.handle(statement, data, context, state);
        if (result == null || result.remainder().isEmpty()) return null;
        return result;
    }
    
    String debugName();
    
}
