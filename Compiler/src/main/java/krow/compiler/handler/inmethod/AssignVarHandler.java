package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssignVarHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<name>" + Signature.IDENTIFIER + ")\\s*=");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String name = matcher.group("name");
        assert (context.getVariable(name) != null);
        context.expectation = CompileExpectation.OBJECT;
        context.child.store = context.getVariable(name);
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "PREPARE_STORE_VARIABLE";
    }
}
