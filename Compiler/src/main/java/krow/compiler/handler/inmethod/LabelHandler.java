package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreLabel;
import krow.compiler.pre.Signature;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^label\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*(?=;)");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("label")) return false;
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, PRIMITIVE, DOWN, UP, SMALL, OBJECT:
                return false;
        }
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String name = matcher.group("name");
        final PreLabel label = context.label(name);
        context.statement(label.instruction());
        context.expectation = CompileExpectation.DEAD_END;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "LABEL";
    }
}
