package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstructorCallStartHandler implements Handler {
    
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
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final boolean here = statement.startsWith("this");
        final PreMethodCall call;
        context.child.preparing.add(0, call = new PreMethodCall());
        context.child.point = null;
        call.dynamic = true;
        call.owner = here ? data.path : data.extend;
        call.name = "<init>";
        context.child.statement.add(WriteInstruction.loadThis());
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_CALL);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_INIT_CALL";
    }
}
