package krow.compiler.handler.inarrayheader;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;

public class ArrayEndHandler implements Handler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith(")");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreArray array = (PreArray) context.child.preparing.remove(0);
        assert array != null;
        if (context.child.point != null) {
            context.child.swap(true);
            int i = array.length;
            context.child.statement((writer, method) -> {
                method.visitInsn(89);
                method.visitIntInsn(16, i);
            });
            context.child.statement(PreVariable.store(array.type, 0));
            array.length++;
        }
        context.child.point = array.type;
        context.child.staticState = false;
        context.expectation = CompileExpectation.NONE;
        final CompileState state = context.child.nested.remove(0);
        return new HandleResult(null, statement.substring(1)
            .trim(), state);
    }
    
    @Override
    public String debugName() {
        return "END_ARRAY_HEADER";
    }
}
