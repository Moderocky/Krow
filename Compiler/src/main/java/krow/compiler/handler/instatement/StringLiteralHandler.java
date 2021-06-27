package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringLiteralHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^\"[^\"\\\\\\r\\n]*(?:\\\\.[^\"\\\\\\r\\n]*)*\"");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, PRIMITIVE, DOWN, UP, METHOD, FIELD:
                return false;
        }
        return statement.startsWith("\"") && PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String value = input.substring(1, input.length() - 1);
        context.child.statement.add(WriteInstruction.loadConstant(value));
        context.expectation = CompileExpectation.NONE;
        final PreVariable assignment;
        if ((assignment = context.child.store) != null) {
            context.child.statement.add(assignment.store(context.child.variables.indexOf(assignment)));
            context.expectation = CompileExpectation.DEAD_END;
            context.child.store = null;
        }
        if (state == CompileState.IN_CALL) {
            context.child.preparing.get(0).addParameter(new Type(String.class));
        }
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "LDC_STRING";
    }
}
