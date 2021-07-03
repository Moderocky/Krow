package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreStructure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StructImplicitHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^struct\\s*\\(");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("struct")) return false;
        switch (context.expectation) {
            case DEAD_END, VARIABLE, SMALL, FIELD, METHOD, MEMBER, DOWN, UP:
                return false;
        }
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final PreStructure structure = new PreStructure();
        context.child.structures.add(0, structure);
        context.child.point = null;
        context.expectation = CompileExpectation.OBJECT;
        context.child.nested.add(0, state);
        context.child.statement(structure.allocate);
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IMPLICIT_STRUCT_HEADER);
    }
    
    @Override
    public String debugName() {
        return "ALLOCATE_STRUCTURE";
    }
}
