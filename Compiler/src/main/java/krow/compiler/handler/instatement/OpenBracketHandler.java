package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreBracket;
import krow.compiler.pre.PreClass;

public class OpenBracketHandler implements DefaultHandler {
    
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
        context.expectation = CompileExpectation.NONE;
        bracket.state = state;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "OPEN_BRACKET";
    }
}
