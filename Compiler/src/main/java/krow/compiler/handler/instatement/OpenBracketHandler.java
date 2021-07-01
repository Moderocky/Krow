package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreBracket;
import krow.compiler.pre.PreClass;

public class OpenBracketHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case DEAD_END, DOWN, UP:
                return false;
        }
        return statement.startsWith("(");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final PreBracket bracket;
        context.brackets().add(0, bracket = new PreBracket());
        bracket.state = state;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "OPEN_BRACKET";
    }
}
