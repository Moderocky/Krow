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
import krow.compiler.util.HiddenModifier;
import krow.compiler.util.SourceReader;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloneHandler implements Handler {
    private static final Pattern PATTERN = Pattern.compile("^clone\\s*?<(?<target>.*?)>");
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("clone") && (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String group = matcher.group("target");
        final Signature signature = new Signature(group.trim(), context.availableTypes().toArray(new Type[0]));
        assert signature.getType() == Signature.Mode.METHOD;
        final PreMethod pre = context.findMethod(signature);
        final Method method = pre.getLoadCopy();
        if (method == null) throw new RuntimeException("Method unavailable to compiler: '" + pre + "'");
        final List<WriteInstruction> instructions = SourceReader.getSource(method);
        context.addUpcoming(HiddenModifier.SYNTHETIC);
        context.child.advance.addAll(instructions);
        return new HandleResult(null, statement.substring(statement.indexOf('>') + 1).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_CLONE";
    }
    
    @Override
    public Library owner() {
        return BinderLibrary.BINDER_LIBRARY;
    }
}
