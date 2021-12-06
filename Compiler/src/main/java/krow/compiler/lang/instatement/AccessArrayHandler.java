package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessArrayHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^\\[\\s*(?!])");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("[")) return false;
        final Type type = context.child.point;
        if (type == null || !type.isArray()) return false;
        if (statement.indexOf(',') > -1 && (statement.indexOf(']') > statement.indexOf(','))) return false;
        return switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final Type type = context.child.point;
        assert type != null;
        assert type.isArray();
        context.expectation = CompileExpectation.SMALL;
        context.child.point = null;
        context.child.nested.add(0, state);
        final PreArray array = new PreArray();
        array.type = type;
        context.child.preparing.add(0, array);
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.ARRAY_ACCESSOR);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_LOAD_ARRAY_ITEM";
    }
}
