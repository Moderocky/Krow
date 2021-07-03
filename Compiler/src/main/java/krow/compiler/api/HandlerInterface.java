package krow.compiler.api;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;

/**
 * A point of interface with a handler holder.
 */
@SuppressWarnings("ALL")
public interface HandlerInterface {
    
    DefaultHandler getHandler(final String statement, final CompileState state, final CompileContext context);
    
}
