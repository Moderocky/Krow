package krow.compiler.api;

import krow.compiler.CompileContext;

/**
 * A point of interface with a handler holder.
 */
@SuppressWarnings("ALL")
public interface HandlerInterface {
    
    Handler getHandler(final String statement, final CompileState state, final CompileContext context);
    
}
