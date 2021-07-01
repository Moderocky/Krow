package krow.compiler.handler.instatement;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CastHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^<\\s*(?<name>" + Signature.TYPE_STRING + ")\\s*>");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        if (!(statement.startsWith("<") && statement.indexOf('>') > 0)) return false;
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final int length = matcher.group().length();
        final String signature = matcher.group("name");
        final Type type = signature.length() > 0 ? new Signature(signature, context.availableTypes()
            .toArray(new Type[0])).getOwner() : new Type(Object.class);
        if (type == null) throw new RuntimeException("Unavailable type: '" + signature + "'");
        if (type.isPrimitive()) {
            final Type previous = context.child.point;
            assert previous != null;
            context.child.point = type;
            context.child.statement(WriteInstruction.convert(previous, type));
        } else {
            context.child.point = type;
            context.child.statement(WriteInstruction.cast(type));
        }
        return new HandleResult(null, statement.substring(length).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "CAST";
    }
}
