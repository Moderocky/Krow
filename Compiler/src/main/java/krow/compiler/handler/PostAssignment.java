package krow.compiler.handler;

import krow.compiler.CompileContext;
import krow.compiler.CompileExpectation;
import krow.compiler.CompileState;
import krow.compiler.pre.PreVariable;

public interface PostAssignment {
    
    
    default void attemptAssignment(final CompileContext context, final CompileState state) {
        final PreVariable assignment;
        if ((assignment = context.child.store) != null && state != CompileState.IN_CALL) { // no assignments during a method call
            context.child.statement.add(assignment.store(context.child.variables.indexOf(assignment)));
            context.expectation = CompileExpectation.DEAD_END;
            context.child.store = null;
        }
    }
}
