package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreBracket;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.Label;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^if\\s+\\(");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        if (!statement.startsWith("if")) return false;
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String input = matcher.group();
        final Label end = new Label();
        context.lookingFor = new Type(boolean.class);
        context.child.statement((writer, method) -> method.visitJumpInsn(153, end));
        context.child.swap(true);
        context.child.skip = (writer, method) -> method.visitLabel(end);
        final PreBracket bracket;
        context.brackets().add(0, bracket = new PreBracket());
        bracket.state = CompileState.IN_METHOD;
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "IF";
    }
}
