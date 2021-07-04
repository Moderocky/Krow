package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.Handler;
import krow.compiler.api.Library;
import krow.compiler.binder.CloneHandler;
import mx.kenzie.foundation.language.PostCompileClass;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("all")
public final class BinderLibrary implements Library, InternalLibrary {
    
    public static final BinderLibrary BINDER_LIBRARY = new BinderLibrary();
    
    private static final HandlerSet DEFAULT_HANDLERS = new HandlerSet();
    
    static {
        DEFAULT_HANDLERS.get(CompileState.CLASS_BODY)
            .add(new CloneHandler());
    }
    
    @Override
    public String identifier() {
        return "krow.binder";
    }
    
    @Override
    public String name() {
        return "Binder";
    }
    
    @Override
    public Collection<Handler> getHandlers(CompileState state) {
        return new ArrayList<>(DEFAULT_HANDLERS.get(state));
    }
    
    @Override
    public Collection<PostCompileClass> getRuntime() {
        return new ArrayList<>();
    }
    
}
