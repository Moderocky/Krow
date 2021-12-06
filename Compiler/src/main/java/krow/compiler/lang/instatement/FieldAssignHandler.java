package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreFieldCall;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class FieldAssignHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^\\.\\s*(?<name>" + Signature.IDENTIFIER + ")\\s*=\\s*(?!=)");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, METHOD, DOWN, UP, LITERAL, VARIABLE, SMALL:
                return false;
        }
        if (context.child.point == null) return false;
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String name = matcher.group("name");
        assert context.child.point != null;
        final PreFieldCall call = new PreFieldCall();
        call.owner = context.child.point;
        call.name = name;
        context.child.skip(call.set(context));
        context.child.point = null;
        context.child.staticState = false;
        context.expectation = CompileExpectation.OBJECT;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "PREPARE_ASSIGN_FIELD";
    }
}
