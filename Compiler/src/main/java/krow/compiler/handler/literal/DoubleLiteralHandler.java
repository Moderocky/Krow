package krow.compiler.handler.literal;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleLiteralHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^\\d+(?:.\\d+)?D(?![\\d.#])");
    private static final int LOW = 48, HIGH = 57;
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        final char c = statement.charAt(0);
        if (c < LOW || c > HIGH) return false;
        return switch (context.expectation) {
            case TYPE, DEAD_END, VARIABLE, DOWN, UP, METHOD, FIELD -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String number = input.substring(0, input.length() - 1);
        final double value = Double.parseDouble(number);
        context.child.point = new Type(double.class);
        context.child.statement(WriteInstruction.loadConstant(value));
        context.expectation = CompileExpectation.NONE;
        if (state == CompileState.CONST_DECLARATION) {
            context.saveConstant.value = value;
            context.expectation = CompileExpectation.DEAD_END;
        }
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "LDC_DOUBLE";
    }
}
