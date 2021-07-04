package krow.compiler.lang.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.opcodes.JavaVersion;

@SuppressWarnings("ALL")
public class DropLevelHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("{");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.hasBody = true;
        if (!context.isInterface && !context.isClass) throw new RuntimeException("No type declaration present.");
        context.builder = new ClassBuilder(new Type(data.path.dotPath()), JavaVersion.JAVA_8)
            .addInterfaces(data.interfaces.toArray(new Type[0]))
            .setSuperclass(data.extend)
            .addModifiers(context.upcoming());
        context.modifiersUpcoming = 0;
        context.availableTypes.add(data.path);
        context.availableTypes.add(data.extend);
        context.availableTypes.addAll(data.interfaces);
        context.createChild();
        return new HandleResult(null, statement.substring(1).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "ENTER_CLASS";
    }
}
