package krow.compiler.pre;

import krow.compiler.CompileContext;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public class PreFieldCall {
    
    public Type owner;
    public String name;
    
    public Type getType(final CompileContext context) {
        return context.findField(owner, name).type;
    }
    
    public WriteInstruction set(final CompileContext context) {
        final PreField field = context.findField(owner, name);
        if (field == null)
            throw new RuntimeException("Unavailable field: '" + owner.getSimpleName() + "." + name + "'");
        if (context.staticState) {
            return WriteInstruction.setStaticField(owner, field.type, field.name);
        }
        return WriteInstruction.setField(owner, field.type, field.name);
    }
    
    public WriteInstruction get(final CompileContext context) {
        final PreField field = context.findField(owner, name);
        if (field == null)
            throw new RuntimeException("Unavailable field: '" + owner.getSimpleName() + "." + name + "'");
        if (context.child.staticState) {
            return WriteInstruction.getStaticField(owner, field.type, field.name);
        }
        return WriteInstruction.getField(owner, field.type, field.name);
    }
    
}
