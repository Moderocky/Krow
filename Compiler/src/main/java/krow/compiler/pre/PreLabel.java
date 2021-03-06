package krow.compiler.pre;

import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Label;

import java.util.ArrayList;
import java.util.List;

public class PreLabel {
    
    protected final List<Label> uses = new ArrayList<>();
    public String name;
    
    public Label use() {
        final Label label;
        uses.add(label = new Label());
        return label;
    }
    
    public WriteInstruction instruction() {
        return (writer, method) -> {
            for (final Label use : uses) {
                method.visitLabel(use);
            }
        };
    }
    
}
