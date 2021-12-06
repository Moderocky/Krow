package krow.compiler.api;

import mx.kenzie.foundation.WriteInstruction;

import java.util.Collection;

public interface ChildContext {
    
    Collection<WriteInstruction> getSkippedInstructions();
    
    Collection<WriteInstruction> getStatement();
    
    void addInstruction(final WriteInstruction instruction);
    
    boolean isBlockAllowed();
    
    void setBlockAllowed(final boolean z);
    
    int getConditionPhase();
    
    void setConditionPhase(final int i);
    
}
