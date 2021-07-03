package krow.compiler.handler.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^library\\s+(?<name>[^\\s;\"])\\s*(?=;)");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("library") && (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String instruction = matcher.group();
        final String group = matcher.group("name");
        context.addCompileTimeLibrary(group.trim());
        if (context.isClass || context.isInterface)
            throw new RuntimeException("Duplicate member declaration: '" + group + "'");
        context.isClass = true;
        data.path = Type.of(group);
        return new HandleResult(null, statement.substring(instruction.length()).trim(), CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "LOAD_COMPILER_LIBRARY";
    }
}
