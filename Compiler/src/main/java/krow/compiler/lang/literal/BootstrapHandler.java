package krow.compiler.handler.literal;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.lang.invoke.CallSite;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BootstrapHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^" + Signature.METHOD_PATTERN.pattern() + "\\s*(?=;)");
    
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
        final Signature signature = new Signature(input);
        final PreMethod method = new PreMethod(signature);
        context.child.point = new Type(CallSite.class);
        context.child.statement(WriteInstruction.loadConstant(method.handle()));
        context.expectation = CompileExpectation.NONE;
        if (state == CompileState.CONST_DECLARATION) {
            context.saveConstant.value = method.handle();
            context.expectation = CompileExpectation.DEAD_END;
        }
        return new HandleResult(null, statement.substring(input.length())
            .trim(), state == CompileState.METHOD_BODY ? CompileState.STATEMENT : state);
    }
    
    @Override
    public String debugName() {
        return "LDC_HANDLE";
    }
}
