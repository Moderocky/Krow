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

public class AssignArrayHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^\\[(?<index>\\d+)]\\s*=");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final int index = Integer.parseInt(matcher.group("index"));
        final Type type = context.child.point;
        assert type != null;
        assert type.isArray();
        context.child.statement(WriteInstruction.push(index));
        context.duplicate = true;
        context.expectation = CompileExpectation.OBJECT;
        context.lookingFor = type.componentType();
        context.child.skip = PreVariable.store(type, 0);
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "PREPARE_STORE_ARRAY";
    }
}
