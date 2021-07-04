package krow.compiler.binder;

import krow.compiler.BinderLibrary;
import krow.compiler.CompileContext;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.api.Handler;
import krow.compiler.api.Library;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodHandleHandler implements Handler {
    private static final Pattern PATTERN = Pattern.compile("^" + Signature.METHOD_PATTERN.pattern());
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String input = matcher.group();
        final Signature signature = new Signature(input.trim(), context.availableTypes().toArray(new Type[0]));
        assert signature.getType() == Signature.Mode.METHOD;
        final PreMethod pre = context.findMethod(signature);
        context.child.statement(WriteInstruction.loadConstant(pre.handle()));
        return new HandleResult(null, statement.substring(input.length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "METHOD_HANDLE";
    }
    
    @Override
    public Library owner() {
        return BinderLibrary.BINDER_LIBRARY;
    }
}
