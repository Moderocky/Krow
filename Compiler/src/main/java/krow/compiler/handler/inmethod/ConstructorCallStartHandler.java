package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstructorCallStartHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^(this|super)\\s*\\(");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("this") && !statement.startsWith("super")) return false;
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL:
                return false;
        }
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final boolean here = statement.startsWith("this");
        final PreMethodCall call;
        context.child.nested.add(0, state == CompileState.METHOD_BODY ? CompileState.STATEMENT : state);
        context.child.preparing.add(0, call = new PreMethodCall());
        context.child.point = null;
        call.dynamic = true;
        call.owner = here ? data.path : data.extend;
        call.name = "<init>";
        context.child.statement(WriteInstruction.loadThis());
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.METHOD_CALL_HEADER);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_INIT_CALL";
    }
}
