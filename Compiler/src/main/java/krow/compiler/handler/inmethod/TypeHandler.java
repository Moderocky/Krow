package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<target>" + Signature.TYPE_STRING + ")(?:\\[])*\\s*(?=[.#])");
    
    String target;
    Type type;
    int length;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case PRIMITIVE, DOWN, UP, MEMBER, METHOD, FIELD, DEAD_END, LITERAL, VARIABLE, SMALL:
                return false;
        }
        final Matcher matcher = PATTERN.matcher(statement);
        if (!matcher.find()) return false;
        final String input = matcher.group();
        length = input.length();
        type = context.resolveType(target = matcher.group("target"));
        return type != null;
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        context.child.point = type;
        context.child.staticState = true;
        context.expectation = CompileExpectation.MEMBER;
        return new HandleResult(null, statement.substring(length)
            .trim(), state == CompileState.IN_METHOD ? CompileState.IN_STATEMENT : state);
    }
    
    @Override
    public String debugName() {
        return "PRE_MEMBER_LOOK";
    }
}
