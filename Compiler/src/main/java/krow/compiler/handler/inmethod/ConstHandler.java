package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreConstant;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^const\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*=");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("const")) return false;
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
        final Map<String, Object> constants = context.getConstants();
        if (constants.containsKey(name)) throw new RuntimeException("Constant already assigned: '" + name + "'");
        context.saveConstant = new PreConstant(name);
        context.exitTo = state;
        context.expectation = CompileExpectation.LITERAL;
        context.lookingFor = new Type(Object.class);
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.CONST_DECLARATION);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_CONSTANT";
    }
}
