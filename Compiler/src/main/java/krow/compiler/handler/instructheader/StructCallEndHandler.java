package krow.compiler.handler.instructheader;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreStructure;

public class StructCallEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith(")");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreStructure structure = context.child.structures.remove(0);
        assert structure != null;
        structure.generate(context);
        context.child.statement(structure.execute(context));
        context.child.staticState = false;
        context.expectation = CompileExpectation.NONE;
        context.child.point = structure.getType();
        final CompileState state = context.child.nested.remove(0);
        return new HandleResult(null, statement.substring(1)
            .trim(), state);
    }
    
    @Override
    public String debugName() {
        return "END_STRUCT";
    }
}
