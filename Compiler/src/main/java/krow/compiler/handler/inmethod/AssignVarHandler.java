package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssignVarHandler implements DefaultHandler {
    
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
        context.duplicate = true;
        context.expectation = CompileExpectation.OBJECT;
        final PreVariable assignment = context.getVariable(name);
        assert (assignment != null);
        context.lookingFor = assignment.type();
        context.child.skip = assignment.store(context.getSlot(assignment));
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "PREPARE_STORE_VARIABLE";
    }
}
