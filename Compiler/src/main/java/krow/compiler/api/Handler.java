package krow.compiler.api;

import krow.compiler.CompileContext;
import krow.compiler.pre.PreClass;

/**
 * A compile-instruction handler.
 * The instruction handler looks for a specific set of circumstances
 * in a specific {@link CompileState} and deals with the source provided.
 * <p>
 * A result is then returned which contains the subsequent state,
 * the remainder of the source and a nullable instruction to be run
 * by the compiler before its next handle phase.
 * <p>
 * The remainder of the source should be everything not dealt with by this handler,
 * with no additional whitespace.
 *
 * @author Moderocky
 */
@SuppressWarnings("ALL")
public interface Handler {
    
    @Deprecated
    default boolean accepts(final String statement) {
        return false;
    }
    
    default boolean accepts(final String statement, final CompileContext context) {
        return accepts(statement);
    }
    
    @Deprecated
    default HandleResult handle(final String statement, final PreClass data, final CompileContext context) {
        throw new IllegalStateException("No handle result provided.");
    }
    
    default HandleResult handle(final String statement, final PreClass data, final CompileContext context, final CompileState state) {
        return handle(statement, data, context);
    }
    
    /**
     * Internal - this should never be overridden.
     */
    @Deprecated
    default HandleResult handle0(final String statement, final PreClass data, final CompileContext context, final CompileState state) {
        final HandleResult result = this.handle(statement, data, context, state);
        if (result == null || result.remainder().isEmpty()) return null;
        return result;
    }
    
    /**
     * The debug name of this handler, used to print the list of used instructions.
     * Formatted in `UPPER_SNAKE_CASE`.
     */
    String debugName();
    
    Library owner();
    
}
