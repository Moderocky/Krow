package krow.compiler.handler.inmethodheader;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.util.HiddenModifier;
import mx.kenzie.foundation.MethodBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.lang.reflect.Modifier;

public class MethodEndParameterHandler implements Handler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(")");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreMethod method = context.method;
        context.availableMethods.add(method);
        assert method.name != null;
        assert method.returnType != null;
        method.modifiers |= context.upcoming();
        final MethodBuilder builder = context.builder
            .addMethod(method.name)
            .setModifiers(method.modifiers);
        if (context.child.exported) builder.addModifiers(Modifier.PUBLIC);
        builder.addParameter(method.parameters.toArray(new Type[0]));
        builder.setReturnType(method.returnType);
        context.expectation = CompileExpectation.DOWN;
        if (context.child.bridgeTarget != null) {
            // TODO better bridge method handler ?
            final PreMethod target = context.child.bridgeTarget;
            int slot = 0;
            assert target.owner.equals(method.owner); // same class
            if (!Modifier.isStatic(target.modifiers)) {
                assert !Modifier.isStatic(method.modifiers);
                builder.writeCode(WriteInstruction.loadThis());
                slot++;
            }
            for (final Type type : target.parameters) {
                builder.writeCode(WriteInstruction.load(type, slot)); // fine to load with target type since Object is A
                builder.writeCode(WriteInstruction.cast(type)); // make sure is of correct type
                slot++;
            }
            builder.writeCode(target.invoke());
            switch (method.returnType.dotPath()) {
                case "int", "boolean", "char", "short", "byte" -> builder.writeCode(WriteInstruction.returnSmall());
                case "long" -> builder.writeCode(WriteInstruction.returnLong());
                case "double" -> builder.writeCode(WriteInstruction.returnDouble());
                case "float" -> builder.writeCode(WriteInstruction.returnFloat());
                case "void" -> builder.writeCode(WriteInstruction.returnEmpty());
                default -> {
                    builder.writeCode(WriteInstruction.cast(method.returnType));
                    builder.writeCode(WriteInstruction.returnObject());
                }
            }
            context.expectation = CompileExpectation.DEAD_END;
        }
        if (context.upcoming(Modifier.ABSTRACT)
            || context.upcoming(Modifier.NATIVE)
            || context.upcoming(HiddenModifier.BRIDGE)
        ) context.expectation = CompileExpectation.DEAD_END;
        context.currentMethod = builder;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "END_OF_PARAMS";
    }
}
