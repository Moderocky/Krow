package krow.compiler.handler.root;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.opcodes.JavaVersion;

public class DropLevelHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("{");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        context.hasBody = true;
        context.child = new CompileContext();
        context.builder = new ClassBuilder(new Type(data.path.dotPath()), JavaVersion.JAVA_8)
            .addInterfaces(data.interfaces.toArray(new Type[0]))
            .setSuperclass(data.extend)
            .addModifiers(context.upcoming());
        context.modifiersUpcoming = 0;
        context.availableTypes.add(data.path);
        context.availableTypes.add(data.extend);
        context.availableTypes.addAll(data.interfaces);
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "ENTER_CLASS";
    }
}
