package krow.compiler.lang.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;
import org.objectweb.asm.Opcodes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class InterfaceHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^interface\\s+(" + Signature.TYPE_STRING + ")");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("interface") && (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String instruction = matcher.group();
        final String group = matcher.group(1);
        if (context.isClass || context.isInterface)
            throw new RuntimeException("Duplicate member declaration: '" + group + "'");
        context.isInterface = true;
        data.path = Type.of(group);
        context.addUpcoming(Opcodes.ACC_INTERFACE);
//        context.addUpcoming(Opcodes.ACC_ABSTRACT);
        return new HandleResult(null, statement.substring(instruction.length()).trim(), CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_INTERFACE";
    }
}
