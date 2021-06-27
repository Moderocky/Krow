package krow.compiler.pre;

import krow.compiler.Resolver;
import mx.kenzie.foundation.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreStructure {
    
    Type type;
    public final Map<String, Type> fields = new HashMap<>();
    PreField[] preFields;
    
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
    
    public Type getType() {
        return type != null ? type : (type = Resolver.resolveStructureType(this));
    }
    
    public PreField[] getFields() {
        if (preFields != null) return preFields;
        final List<PreField> fields = new ArrayList<>();
        for (final Map.Entry<String, Type> entry : this.fields.entrySet()) {
            fields.add(new PreField(getType(), entry.getValue(), entry.getKey()));
        }
        return preFields = fields.toArray(new PreField[0]);
    }
}
