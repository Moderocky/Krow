package krow.compiler;

import krow.compiler.api.Library;
import mx.kenzie.foundation.WriteInstruction;

import java.util.ArrayList;
import java.util.List;

public class BlockSectionContext extends WorkContext {
    
    final List<WriteInstruction> onClose = new ArrayList<>();
    private final Library owner;
    
    public BlockSectionContext(Library owner) {
        this.owner = owner;
    }
    
    @Override
    public BlockSectionContext child() {
        return null; // can't have children - stacked in child context
    }
    
    @Override
    public void statement(final WriteInstruction instruction) {
        // Brackets handled in CompileContext
        statementRaw(instruction);
    }
    
    public WriteInstruction complete() { // one instruction for potential swap
        final List<WriteInstruction> list = statement();
        list.addAll(onClose);
        return (writer, visitor) -> {
            for (final WriteInstruction instruction : list) {
                instruction.accept(writer, visitor);
            }
        };
    }
    
    public Library getOwner() {
        return owner;
    }
}
