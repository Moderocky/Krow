package krow.compiler;

import krow.compiler.api.Library;

import java.util.List;

@SuppressWarnings("ALL")
public interface KrowCompiler {
    
    List<Library> getLibraries();
    
    void registerLibrary(final Library library);
    
}
