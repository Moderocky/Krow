package krow.compiler;

import krow.compiler.api.Library;
import mx.kenzie.foundation.CodeWriter;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;

public class BlockSectionContext extends WorkContext {
    
    public final List<WriteInstruction> onClose = new ArrayList<>();
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
        return new BlockInstruction(list);
    }
    
    public Library getOwner() {
        return owner;
    }
    
    public record BlockInstruction(List<WriteInstruction> list) implements WriteInstruction {
        
        @Override
        public void accept(CodeWriter writer, MethodVisitor visitor) {
            for (final WriteInstruction instruction : list) {
                instruction.accept(writer, visitor);
            }
        }
    }
}
