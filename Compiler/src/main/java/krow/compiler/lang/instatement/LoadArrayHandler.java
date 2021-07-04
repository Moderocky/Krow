package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadArrayHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^\\[(?<index>\\d+)]\\s*(?!=)");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final int index = Integer.parseInt(matcher.group("index"));
        final Type type = context.child.point;
        assert type != null;
        assert type.isArray();
        context.child.statement(WriteInstruction.push(index));
        context.child.statement(PreVariable.load(type, 0));
        context.child.point = type.componentType();
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "LOAD_ARRAY_ITEM";
    }
}
