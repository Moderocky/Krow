package krow.compiler.lang.instatement;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.Collections;

@SuppressWarnings("ALL")
public class DeadEndHandler implements DefaultHandler {
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith(";");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        if (context.child.inverted)
            Collections.reverse(context.child.statement());
        if (context.child.skip != null)
            context.child.statement(context.child.skip);
        else if (context.child.point != null)
            context.child.statement(WriteInstruction.pop()); // assume dead type and no handler
        if (context.child.awaitAdjustedType && context.child.point != null) {
            final Type type = context.child.point;
            if (context.child.inReturnPhase) {
                context.currentMethod.setReturnType(type);
                context.method.returnType = type;
            } else if (context.child.forAdjustment != null) {
                context.child.forAdjustment.type = type;
            }
        }
        context.currentMethod.writeCode(context.child.statement().toArray(new WriteInstruction[0]));
        context.child.preparing.clear();
        context.child.statement().clear();
        context.child.expectation = CompileExpectation.NONE;
        context.child.store = null;
        context.child.skip = null;
        context.child.staticState = false;
        context.child.awaitAdjustedType = false;
        context.child.inReturnPhase = false;
        context.child.point = null;
        context.child.nested.clear();
        context.lookingFor = null;
        context.duplicate = false;
        context.expectation = CompileExpectation.NONE;
        return new HandleResult(null, statement.substring(1).trim(), CompileState.METHOD_BODY);
    }
    
    @Override
    public String debugName() {
        return "END_OF_STATEMENT";
    }
}
