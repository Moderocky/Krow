package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreLabel;
import krow.compiler.pre.Signature;
import org.objectweb.asm.Label;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GotoHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^goto\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*(?=;)");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        if (!statement.startsWith("goto")) return false;
        return switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, PRIMITIVE, DOWN, UP, SMALL, OBJECT -> false;
            default -> (matcher = PATTERN.matcher(statement)).find();
        };
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final String name = matcher.group("name");
        final PreLabel label = context.label(name);
        final Label to = label.use();
        context.statement((writer, method) -> method.visitJumpInsn(167, to));
        context.expectation = CompileExpectation.DEAD_END;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "JUMP";
    }
}
