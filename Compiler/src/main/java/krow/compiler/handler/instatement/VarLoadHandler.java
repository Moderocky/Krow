package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.handler.PostAssignment;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.WriteInstruction;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarLoadHandler implements Handler, PostAssignment {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<name>" + Signature.IDENTIFIER + ")");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, METHOD, FIELD:
                return false;
        }
        return PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String input = matcher.group();
        final String name = matcher.group("name");
        if (Objects.equals(name, "this")) {
            context.child.statement.add(WriteInstruction.loadThis());
        } else {
            final PreVariable variable = context.child.getVariable(name);
            assert variable != null;
            context.child.statement.add(variable.load(context.child.variables.indexOf(variable)));
        }
        context.expectation = CompileExpectation.NONE;
        attemptAssignment(context, state);
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "LOAD_VAR";
    }
}
