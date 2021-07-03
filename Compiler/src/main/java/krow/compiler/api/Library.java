package krow.compiler.api;

import krow.compiler.DefaultHandler;

import java.util.Collection;

/**
 * A compile-time library that a class can use,
 * which adds pre-compilation functionality.
 * <p>
 * The intended use of this is to register new keywords/operators.
 *
 * @author Moderocky
 */
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
    
    Collection<DefaultHandler> getHandlers(final CompileState state);
    
}
