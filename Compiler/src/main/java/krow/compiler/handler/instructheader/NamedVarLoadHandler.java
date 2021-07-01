package krow.compiler.handler.instructheader;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreStructure;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedVarLoadHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<name>" + Signature.IDENTIFIER + ")");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, METHOD, FIELD:
                return false;
        }
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        assert !context.child.structures.isEmpty();
        final PreStructure structure = context.child.structures.get(0);
        assert structure != null;
        final Type type;
        final String name = matcher.group("name");
        if (context.hasConstant(name)) {
            final Object value = context.getConstant(name);
            context.child.statement(WriteInstruction.loadConstant(value));
            context.child.point = type = new Type(value.getClass());
        } else {
            final PreVariable variable = context.child.getVariable(name);
            if (variable == null) {
                if ("1".equals(System.getProperty("TEST_STATE"))) {
                    final List<String> strings = new ArrayList<>();
                    for (final PreVariable var : context.child.variables) {
                        strings.add(var.name());
                    }
                    throw new RuntimeException("Unavailable variable: '" + name + "'\nAvailable: " + strings);
                } else throw new RuntimeException("Unavailable variable: '" + name + "'");
            }
            context.child.statement(variable.load(context.child.variables.indexOf(variable)));
            context.child.point = type = variable.type();
        }
        structure.fields.put(name, type);
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "LOAD_VAR_ARG";
    }
}
