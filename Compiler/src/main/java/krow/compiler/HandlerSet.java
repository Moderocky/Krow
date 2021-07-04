package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.Handler;
import krow.compiler.api.HandlerInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
final class HandlerSet extends HashMap<CompileState, List<Handler>> implements HandlerInterface {
    
    public HandlerSet(Map<? extends CompileState, ? extends List<Handler>> m) {
        super(m);
    }
    
    public HandlerSet() {
        for (final CompileState value : CompileState.values()) {
            this.put(value, new ArrayList<>());
        }
    }
    
    public Handler getHandler(final String statement, final CompileState state, final CompileContext context) {
        for (final Handler handler : this.get(state)) {
            if (handler.accepts(statement, context)) return handler;
        }
        final String help;
        if (statement.length() > 17) help = statement.substring(0, 17) + "...";
        else help = statement;
        throw new RuntimeException("No handler matches: '" + help + "' in " + state.name());
    }
    
}
