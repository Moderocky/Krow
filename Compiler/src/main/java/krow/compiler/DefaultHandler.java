package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.api.Handler;
import krow.compiler.api.Library;
import krow.compiler.pre.PreClass;

public interface DefaultHandler extends Handler {
    
    default boolean accepts(final String statement, final CompileContext context) {
        return accepts(statement);
    }
    
    @Deprecated
    default boolean accepts(final String statement) {
        return false;
    }
    
    @Deprecated
    default HandleResult handle0(final String statement, final PreClass data, final CompileContext context, final CompileState state) {
        final HandleResult result = this.handle(statement, data, context, state);
        if (result == null || result.remainder().isEmpty()) return null;
        return result;
    }
    
    default HandleResult handle(final String statement, final PreClass data, final CompileContext context, final CompileState state) {
        return handle(statement, data, context);
    }
    
    @Deprecated
    default HandleResult handle(final String statement, final PreClass data, final CompileContext context) {
        throw new IllegalStateException("No handle result provided.");
    }
    
    String debugName();
    
    @Override
    default Library owner() {
        return SystemLibrary.SYSTEM_LIBRARY;
    }
}
