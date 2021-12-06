package krow.compiler.lang.inmatrixheader;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
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
        if (context.child.point != null) {
            context.child.swap(true);
            final int[] coords = array.incrementMatrixSlotCoordinates();
            context.child.statement((writer, method) -> {
                method.visitInsn(89);
                method.visitIntInsn(16, coords[0]);
                PreVariable.load(array.type, 0).accept(writer, method);
                method.visitIntInsn(16, coords[1]);
            });
            context.child.statement(PreVariable.store(array.type.componentType(), 0));
            context.child.point = null;
        }
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IMPLICIT_MATRIX_HEADER);
    }
    
    @Override
    public String debugName() {
        return "NEXT_ARG";
    }
}
