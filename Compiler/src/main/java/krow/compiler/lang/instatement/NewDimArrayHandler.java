package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewDimArrayHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^new\\s+(?<type>" + Signature.TYPE_STRING + "\\s*(?:\\[\\d+])+)\\s*(?!\\()");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("new")) return false;
        return switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, FIELD, DOWN, UP -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String target = matcher.group("type");
        final Type type = context.resolveType(target);
        assert type != null;
        assert type.isArray();
        final PreArray array = new PreArray();
        array.type = type;
        final int[] dimensions = count(input);
        array.dimensions = dimensions[0];
        context.child.statement(array.create(dimensions));
        context.child.point = array.type;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "ALLOCATE_MULTI_ARRAY";
    }
    
    protected int[] count(String input) {
        List<Integer> list = new ArrayList<>();
        while (input.indexOf('[') > -1) {
            input = input.substring(input.indexOf('[') + 1);
            int index = input.indexOf(']');
            list.add(Integer.valueOf(input.substring(0, index)));
            input = input.substring(index + 1);
        }
        Integer[] integers = list.toArray(new Integer[0]);
        int[] ints = new int[integers.length];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = integers[i];
        }
        return ints;
    }
}
