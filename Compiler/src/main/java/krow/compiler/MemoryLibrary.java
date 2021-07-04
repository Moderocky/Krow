package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.Handler;
import krow.compiler.api.Library;
import mx.kenzie.foundation.language.PostCompileClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MemoryLibrary implements Library, InternalLibrary {
    
    static final MemoryLibrary MEMORY_LIBRARY = new MemoryLibrary();
    
    private static final HandlerSet DEFAULT_HANDLERS = new HandlerSet();

    static {
    }
    
    private final List<PostCompileClass> list;
    
    {
        if ("2".equals(System.getProperty("TEST_STATE"))) list = new ArrayList<>();
        else list = List.of(
            InternalLibrary.getRuntimeDependency("krow.memory.Overseer", "Overseer.class")
        );
    }
    
    @Override
    public String identifier() {
        return "krow.memory";
    }
    
    @Override
    public String name() {
        return "Memory";
    }
    
    @Override
    public Collection<Handler> getHandlers(CompileState state) {
        return new ArrayList<>(DEFAULT_HANDLERS.get(state));
    }
    
    @Override
    public Collection<PostCompileClass> getRuntime() {
        if ("2".equals(System.getProperty("TEST_STATE"))) return new ArrayList<>();
        return list;
    }
    
}
