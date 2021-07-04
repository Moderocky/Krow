package krow.compiler.handler.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class ExtendsHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^extends\\s+(" + Signature.TYPE_STRING + ")");
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("extends") && PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String instruction = matcher.group();
        final String group = matcher.group(1);
        data.extend = context.resolveType(group);
        return new HandleResult(null, statement.substring(instruction.length()).trim(), CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_CLASS";
    }
}
