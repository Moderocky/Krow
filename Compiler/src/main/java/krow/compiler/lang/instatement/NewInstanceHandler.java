package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewInstanceHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^new\\s+(?<type>" + Signature.TYPE_STRING + ")\\s*\\(");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("new")) return false;
        return switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, FIELD, DOWN, UP -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String target = matcher.group("type");
        final Type type = context.resolveType(target);
        assert type != null;
        context.child.statement(WriteInstruction.allocate(type));
        if (context.duplicate) {
            context.child.statement(WriteInstruction.duplicate());
            context.duplicate = false;
        }
        context.child.point = type;
        final PreMethodCall call;
        context.child.nested.add(0, state == CompileState.METHOD_BODY ? CompileState.STATEMENT : state);
        context.child.preparing.add(0, call = new PreMethodCall());
        context.child.point = null;
        call.dynamic = true;
        call.owner = type;
        call.name = "<init>";
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.METHOD_CALL_HEADER);
    }
    
    @Override
    public String debugName() {
        return "ALLOCATE_TOP";
    }
}
