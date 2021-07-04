package krow.compiler.handler.instructheader;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreStructure;

@SuppressWarnings("ALL")
public class StructCallEndHandler implements DefaultHandler {
    
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
