package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.Resolver;
import krow.lang.Structure;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.opcodes.JavaVersion;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static mx.kenzie.foundation.WriteInstruction.*;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.DUP;

@SuppressWarnings("ALL")
public class PreStructure {
    
    public final Map<String, Type> fields = new LinkedHashMap<>();
    public final WriteInstruction allocate = new AlterableWriteInstruction();
    PreField[] preFields;
    private Type type;
    
    public PreStructure() {
    
    }
    
    public PreStructure(final String[] names, final Type[] types) {
        assert names.length == types.length;
        for (int i = 0; i < names.length; i++) {
            fields.put(names[i], types[i]);
        }
        type = Resolver.resolveStructureType(this);
    }
    
    @Override
    public String toString() {
        final List<String> list = new ArrayList<>();
        for (final Map.Entry<String, Type> entry : fields.entrySet()) {
            list.add(entry.getKey() + ":" + entry.getValue().internalName());
        }
        return "S(" + String.join(",", list) + ")";
    }
    
    public PreField[] getFields() {
        if (preFields != null) return preFields;
        final List<PreField> fields = new ArrayList<>();
        for (final Map.Entry<String, Type> entry : this.fields.entrySet()) {
            fields.add(new PreField(getType(), entry.getValue(), entry.getKey()));
        }
        return preFields = fields.toArray(new PreField[0]);
    }
    
    public Type getType() {
        return type != null ? type : (type = Resolver.resolveStructureType(this));
    }
    
    public void generate(final CompileContext context) {
        final ClassBuilder builder;
        final JavaVersion version;
        //noinspection UnusedAssignment
        context.builder.suppress(builder = new ClassBuilder(getType(), version = context.builder.getVersion()));
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL).setSuperclass(new Type(Structure.class));
        final MethodBuilder constructor = builder.addConstructor().addModifiers(Modifier.PUBLIC);
        constructor.writeCode((writer, method) -> {
            method.visitVarInsn(25, 0);
            method.visitMethodInsn(183, "krow/lang/Structure", "<init>", "()V", false);
        });
        int i = 1;
        final List<PreField> list = new ArrayList<>();
        for (Map.Entry<String, Type> entry : fields.entrySet()) {
            list.add(new PreField(getType(), entry.getValue(), entry.getKey()));
            builder.addField(entry.getKey())
                .addModifiers(ACC_PUBLIC)
                .setType(entry.getValue());
            constructor.addParameter(entry.getValue());
            constructor.writeCode(
                loadThis(),
                load(entry.getValue(), i),
                setField(getType(), entry.getValue(), entry.getKey())
            );
            i++;
        }
        constructor.writeCode(returnEmpty());
        context.availableFields.addAll(list);
    }
    
    public WriteInstruction execute(final CompileContext context) {
        PreMethod method = new PreMethod();
        method.returnType = new Type(void.class);
        method.name = "<init>";
        method.parameters.addAll(fields.values());
        method.owner = getType();
        return method.execute(context, true);
    }
    
    public class AlterableWriteInstruction implements WriteInstruction {
        
        @Override
        public void accept(CodeWriter writer, MethodVisitor method) {
            method.visitTypeInsn(187, type.internalName());
            method.visitInsn(DUP);
        }
    }
    
}
