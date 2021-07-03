package krow.compiler.handler.inarrayheader;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;

public class ArraySplitParameterHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(",");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreArray array = (PreArray) context.child.preparing.get(0);
        if (context.child.point != null) {
            context.child.swap(true);
            int i = array.length;
            context.child.statement((writer, method) -> {
                method.visitInsn(89);
                method.visitIntInsn(16, i);
            });
            context.child.statement(PreVariable.store(array.type, 0));
            array.length++;
            context.child.point = null;
        }
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IMPLICIT_ARRAY_HEADER);
    }
    
    @Override
    public String debugName() {
        return "NEXT_ARG";
    }
}
