package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarLoadHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<name>" + Signature.IDENTIFIER + ")");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, METHOD, FIELD, DOWN, UP -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String name = matcher.group("name");
        if (Objects.equals(name, "this")) {
            context.child.statement(WriteInstruction.loadThis());
            context.child.point = data.path;
        } else if (Objects.equals(name, "super")) {
            context.child.statement(WriteInstruction.loadThis());
            context.child.point = data.extend;
        } else if (context.hasConstant(name)) {
            final Object value = context.getConstant(name);
            context.child.statement(WriteInstruction.loadConstant(value));
            context.child.point = new Type(value.getClass());
        } else {
            final PreVariable variable = context.getVariable(name);
            if (variable == null) {
                if ("1".equals(System.getProperty("TEST_STATE"))) {
                    final List<String> strings = new ArrayList<>();
                    for (final PreVariable var : context.child.variables) {
                        strings.add(var.name());
                    }
                    throw new RuntimeException("Unavailable variable: '" + name + "'\nAvailable: " + strings);
                } else throw new RuntimeException("Unavailable variable: '" + name + "'");
            }
            context.child.point = variable.type();
            context.child.statement(variable.load(context.getSlot(variable)));
        }
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(input.length())
            .trim(), state == CompileState.METHOD_BODY ? CompileState.STATEMENT : state);
    }
    
    @Override
    public String debugName() {
        return "LOAD_VAR";
    }
}
