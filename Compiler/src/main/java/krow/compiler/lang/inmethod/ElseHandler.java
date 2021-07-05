package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

public class ElseHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        if (context.getConditionPhase() % 2 == 0) return false;
        return (statement.startsWith("else"));
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Label end = new Label();
        final IfHandler.PassJumpInstruction instruction = context.child.elseJumps.get(0);
        instruction.jump = (writer, method) -> method.visitJumpInsn(Opcodes.GOTO, end);
        context.child.skip((writer, method) -> method.visitLabel(end));
        context.child.setBlockAllowed(true);
        context.decayConditionPhase();
        return new HandleResult(null, statement.substring(4).trim(), CompileState.METHOD_BODY);
    }
    
    @Override
    public String debugName() {
        return "ELSE";
    }
}
