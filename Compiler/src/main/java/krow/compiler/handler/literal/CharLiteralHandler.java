package krow.compiler.handler.literal;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class CharLiteralHandler implements DefaultHandler {

//    private static final Pattern PATTERN = Pattern.compile("^'(?:[^'\\\\\\r\\n]|\\\\['tbnrf\\\\])'");
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, PRIMITIVE, DOWN, UP, METHOD, FIELD:
                return false;
        }
        if (statement.charAt(0) != '\'') return false;
        if (statement.charAt(2) != '\'') {
            if (statement.charAt(3) == '\'' && statement.charAt(1) == '\\') return true;
            if (statement.charAt(1) == '\\' && statement.charAt(2) == 'u')
                return statement.charAt(7) == '\'';
        }
        return true;
        // return statement.startsWith("\"") && PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final boolean escaped = statement.charAt(1) == '\\';
        final boolean unicode = statement.charAt(2) == 'u';
        final char value;
        final int length;
        if (unicode) {
            value = (char) Integer.parseInt(statement.substring(3, 7), 16);
            length = 8;
        } else if (escaped) {
            value = switch (statement.charAt(2)) {
                case 't' -> '\t';
                case 'b' -> '\b';
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 'f' -> '\f';
                default -> '\0';
            };
            length = 4;
        } else {
            value = statement.charAt(1);
            length = 3;
        }
        context.child.point = new Type(char.class);
        context.child.statement(WriteInstruction.loadConstant(value));
        context.expectation = CompileExpectation.NONE;
        if (state == CompileState.CONST_DECLARATION) {
            context.saveConstant.value = value;
            context.expectation = CompileExpectation.DEAD_END;
        }
        return new HandleResult(null, statement.substring(length).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "LDC_CHAR";
    }
}
