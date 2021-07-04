package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.util.Handles;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Handle;

public class PreFieldCall {
    
    public Type owner;
    public String name;
    public boolean dynamic;
    
    public Type getType(final CompileContext context) {
        final PreField field = context.findField(owner, name);
        if (field == null) return null;
        return field.type;
    }
    
    public WriteInstruction set(final CompileContext context) {
        if (dynamic) return setDynamic(context);
        final PreField field = context.findField(owner, name);
        if (field == null)
            throw new RuntimeException("Unavailable field: '" + owner.getSimpleName() + "." + name + "'");
        if (context.child.staticState) {
            return WriteInstruction.setStaticField(owner, field.type, field.name);
        }
        return WriteInstruction.setField(owner, field.type, field.name);
    }
    
    protected WriteInstruction setDynamic(final CompileContext context) {
        final PreField field = context.findField(owner, name);
        if (field == null)
            throw new RuntimeException("Unavailable field: '" + owner.getSimpleName() + "." + name + "'");
        return invokeFieldDynamic(field, !context.child.staticState, true);
    }
    
    public WriteInstruction invokeFieldDynamic(PreField field, boolean dynamic, boolean set) {
        final Handle bootstrap = Handles.getPBFG(dynamic, set);
        if (set)
            return WriteInstruction.invokeDynamic(new Type(void.class), name, new Type[]{field.type}, bootstrap, org.objectweb.asm.Type.getType(owner.descriptor()));
        else {
            return WriteInstruction.invokeDynamic(field.type, name, new Type[0], bootstrap, org.objectweb.asm.Type.getType(owner.descriptor()));
        }
    }
    
    public WriteInstruction get(final CompileContext context) {
        if (dynamic) return getDynamic(context);
        final PreField field = context.findField(owner, name);
        if (field == null)
            throw new RuntimeException("Unavailable field: '" + owner.getSimpleName() + "." + name + "'");
        if (context.child.staticState) {
            return WriteInstruction.getStaticField(owner, field.type, field.name);
        }
        return WriteInstruction.getField(owner, field.type, field.name);
    }
    
    protected WriteInstruction getDynamic(final CompileContext context) {
        final PreField field = context.findField(owner, name);
        if (field == null)
            throw new RuntimeException("Unavailable field: '" + owner.getSimpleName() + "." + name + "'");
        return invokeFieldDynamic(field, !context.child.staticState, false);
    }
    
}
