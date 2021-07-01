package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class CastHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("<") && statement.indexOf('>') > 0;
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final int length;
        final String signature = statement.substring(1, length = statement.indexOf('>')).trim();
        final Type type = signature.length() > 0 ? new Signature(signature, context.availableTypes()
            .toArray(new Type[0])).getOwner() : new Type(Object.class);
        if (type == null) throw new RuntimeException("Unavailable type: '" + signature + "'");
        if (type.isPrimitive()) {
            final Type previous = context.child.point;
            assert previous != null;
            context.child.statement(WriteInstruction.convert(previous, type));
        } else context.child.statement(WriteInstruction.cast(type));
        context.child.point = type;
        return new HandleResult(null, statement.substring(length + 1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "CAST";
    }
}
