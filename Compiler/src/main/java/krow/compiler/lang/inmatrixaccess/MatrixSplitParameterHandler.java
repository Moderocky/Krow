package krow.compiler.lang.inmatrixaccess;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;

@SuppressWarnings("ALL")
public class MatrixSplitParameterHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(",");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreArray array = (PreArray) context.child.preparing.get(0);
        assert array != null;
        context.child.statement(PreVariable.load(array.type, 0));
        context.child.point = array.type.componentType();
        context.child.staticState = false;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.MATRIX_ACCESSOR);
    }
    
    @Override
    public String debugName() {
        return "NEXT_ARG";
    }
}
