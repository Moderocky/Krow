package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreBracket;
import krow.compiler.pre.PreClass;

public class CloseBracketHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case VARIABLE, SMALL, OBJECT, PRIMITIVE, DOWN, UP:
                return false;
        }
        return statement.startsWith(")") && !context.brackets().isEmpty();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final PreBracket bracket = context.brackets().remove(0);
        bracket.close(context);
        bracket.exitType = context.child.point;
        return new HandleResult(null, statement.substring(1).trim(), bracket.state);
    }
    
    @Override
    public String debugName() {
        return "CLOSE_BRACKET";
    }
}
