package krow.compiler.api;

import mx.kenzie.foundation.language.PostCompileClass;

import java.util.Collection;

/**
 * A compile-time library that a class can use,
 * which adds pre-compilation functionality.
 * <p>
 * The intended use of this is to register new keywords/operators.
 *
 * @author Moderocky
 */
@SuppressWarnings("ALL")
public interface Library {
    
    /**
     * An identifier for this library.
     * Used by the compiler to find it.
     *
     * @return the identifier
     */
    String identifier();
    
    /**
     * The name of the library, to use in debug reports.
     */
    String name();
    
    /**
     * Source-level language handlers for a given compile state.
     */
    Collection<Handler> getHandlers(final CompileState state);
    
    /**
     * Runtime dependencies to be included in complete archives.
     */
    Collection<PostCompileClass> getRuntime();
    
}
