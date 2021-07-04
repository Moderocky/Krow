package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

@SuppressWarnings("ALL")
public class ClassSuffixHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith(".class") && context.child.point != null;
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final Type type = context.child.point;
        if (type == null) throw new RuntimeException("The dot-class constant must succeed a type.");
        if (!context.child.staticState)
            throw new RuntimeException("The dot-class constant must succeed only raw types.");
        context.child.point = new Type(Class.class);
        context.child.statement(WriteInstruction.loadConstant(type));
        return new HandleResult(null, statement.substring(6).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "DOT_CLASS";
    }
}
