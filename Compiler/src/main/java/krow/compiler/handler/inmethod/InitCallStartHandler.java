package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethodCall;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InitCallStartHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<name>" + Signature.IDENTIFIER + ")\\s*\\(");
    
    Matcher matcher;
    String name;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, FIELD, UP, DOWN:
                return false;
        }
        matcher = PATTERN.matcher(statement);
        if (!matcher.find()) return false;
        return context.hasVariable(name = matcher.group("name"));
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String input = matcher.group();
        final PreMethodCall call;
        final PreVariable variable = context.getVariable(name);
        assert variable != null;
        context.child.preparing.add(0, call = new PreMethodCall());
        context.child.point = null;
        call.dynamic = true;
        call.owner = variable.type();
        call.name = "<init>";
        context.child.statement.add(variable.load(context.getSlot(variable)));
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_CALL);
    }
    
    @Override
    public String debugName() {
        return "BEGIN_INIT_CALL";
    }
}
