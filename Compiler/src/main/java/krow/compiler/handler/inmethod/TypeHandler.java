package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class TypeHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?<target>" + Signature.TYPE_STRING + ")\\s*(?=[.#])");
    
    String target;
    Type type;
    int length;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case DOWN, UP, DEAD_END, LITERAL, VARIABLE:
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
            .trim(), state == CompileState.METHOD_BODY ? CompileState.STATEMENT : state);
    }
    
    @Override
    public String debugName() {
        return "PRE_MEMBER_LOOK";
    }
}
