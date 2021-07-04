package krow.compiler;

import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.pre.*;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class WorkContext {
    public final List<PreVariable> variables = new ArrayList<>();
    public final List<WriteInstruction> statement = new ArrayList<>();
    public final List<WriteInstruction> advance = new ArrayList<>();
    public final List<PreMethodCall> preparing = new ArrayList<>();
    public final List<PreStructure> structures = new ArrayList<>();
    public final List<Object> exports = new ArrayList<>();
    public final List<CompileState> nested = new ArrayList<>();
    protected final List<PreBracket> brackets = new ArrayList<>();
    protected final List<PreLabel> labels = new ArrayList<>();
    public CompileExpectation expectation = CompileExpectation.NONE;
    public WriteInstruction skip; // stacked when DEAD_END reached (eol)
    public WriteInstruction doAfter; // done after swap
    public Function<Type, WriteInstruction> doExpecting; // done after swap
    public PreVariable store;
    public Type point;
    public Type lookingFor;
    public Type pointAfter;
    public boolean duplicate;
    public boolean staticState;
    public boolean inverted;
    public boolean exported;
    public boolean hasBody;
    public boolean inReturnPhase;
    public boolean awaitAdjustedType;
    public PreVariable forAdjustment;
    public PreConstant saveConstant;
    public CompileState exitTo;
    private boolean swap;
    
    public abstract WorkContext child();
    
    public List<WriteInstruction> statement() {
        if (brackets.isEmpty())
            return child() != null ? child().statement : statement;
        else return brackets.get(0).statement();
    }
    
    public void statement(final WriteInstruction instruction) {
        if (!brackets.isEmpty()) brackets.get(0).statement(instruction);
        else if (child() == null) statementRaw(instruction);
        else child().statement(instruction);
    }
    
    public void statementRaw(final WriteInstruction instruction) {
        final List<WriteInstruction> list = statement;
        if (swap()) {
            list.add(list.size() - 1, instruction);
            swap(false);
        } else list.add(instruction);
        final WriteInstruction after = doAfter();
        if (after != null) list.add(after);
        final Function<Type, WriteInstruction> expecting = doExpecting();
        if (pointAfter != null) {
            point = pointAfter;
            pointAfter = null;
        }
        if (expecting != null) list.add(expecting.apply(point));
    }
    
    public Function<Type, WriteInstruction> doExpecting() {
        final WorkContext context;
        final Function<Type, WriteInstruction> after;
        context = child() == null ? this : child();
        after = context.doExpecting;
        context.doExpecting = null;
        return after;
    }
    
    public WriteInstruction doAfter() {
        final WorkContext context;
        final WriteInstruction after;
        context = child() == null ? this : child();
        after = context.doAfter;
        context.doAfter = null;
        return after;
    }
    
    public boolean swap() {
        if (!brackets.isEmpty()) return brackets.get(0).swap();
        return child() != null ? child().swap() : swap;
    }
    
    public void swap(final boolean value) {
        if (!brackets.isEmpty()) brackets.get(0).swap(value);
        else if (child() != null) child().swap(value);
        else {
            swap = value;
        }
    }
    
}
