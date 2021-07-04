package krow.compiler.pre;

import krow.compiler.CompileContext;
import krow.compiler.Resolver;
import mx.kenzie.foundation.Type;

import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class Signature {
    
    public static final String IDENTIFIER = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    public static final String TYPE_STRING = IDENTIFIER + "(/" + IDENTIFIER + ")*(?:\\[])*";
    public static final String STRUCTURE_STRING = "S\\(" + IDENTIFIER + ":" + TYPE_STRING + "(," + IDENTIFIER + ":" + TYPE_STRING + ")*" + "\\)";
    public static final String CL_TYPE_STRING = "(" + TYPE_STRING + "|" + STRUCTURE_STRING + ")";
    public static final Pattern METHOD_PATTERN = Pattern.compile(TYPE_STRING + "::" + IDENTIFIER + "\\(" + "(" + CL_TYPE_STRING + ",?)*" + "\\)" + CL_TYPE_STRING);
    public static final Pattern STRUCTURE_PATTERN = Pattern.compile(STRUCTURE_STRING);
    public static final Pattern TYPE_PATTERN = Pattern.compile(TYPE_STRING);
    public static final Pattern FIELD_PATTERN = Pattern.compile(TYPE_STRING + "\\." + IDENTIFIER + ":" + TYPE_STRING);
    public static final String ARRAY_TYPE_STRING = IDENTIFIER + "(/" + IDENTIFIER + ")*(?:\\[])+";
    protected String value;
    protected Mode mode;
    protected mx.kenzie.foundation.Type owner;
    protected String name;
    protected mx.kenzie.foundation.Type bound;
    protected mx.kenzie.foundation.Type[] inside;
    
    Signature() {
    
    }
    
    public Signature(final String value, final Type... available) {
        this(value, Mode.getType(value), available);
    }
    
    public Signature(final String value, final Mode mode, final Type... available) {
        this.value = value;
        this.mode = mode;
        
        switch (mode) {
            case TYPE -> {
                owner = Resolver.resolveType(value, available);
                name = null;
                bound = null;
                inside = new Type[0];
            }
            case FIELD -> {
                owner = Resolver.resolveType(value.substring(0, value.indexOf('.')), available);
                name = value.substring(value.indexOf('.') + 1, value.indexOf(':'));
                bound = Resolver.resolveType(value.substring(value.indexOf(':') + 1), available);
                inside = new Type[0];
            }
            case METHOD -> {
                owner = Resolver.resolveType(value.substring(0, value.indexOf(':')), available);
                name = value.substring(value.indexOf("::") + 2, value.indexOf('('));
                bound = Resolver.resolveType(value.substring(value.indexOf(')') + 1), available);
                final String sub = value.substring(value.indexOf('(') + 1, value.indexOf(')')).trim();
                if (sub.isBlank()) inside = new Type[0];
                else {
                    final String[] parts = sub.split(",");
                    inside = new Type[parts.length];
                    for (int i = 0; i < parts.length; i++) {
                        inside[i] = Resolver.resolveType(parts[i], available);
                    }
                }
            }
            case STRUCTURE -> {
                final PreStructure structure = Resolver.resolveStructure(value, available);
                owner = Resolver.resolveStructureType(structure);
                name = value;
                bound = null;
                inside = structure.fields.values().toArray(new Type[0]);
            }
        }
    }
    
    public Signature(final String value) {
        this(value, Mode.getType(value));
    }
    
    public String getValue() {
        return value;
    }
    
    public Mode getType() {
        return mode;
    }
    
    public Type getOwner() {
        return owner;
    }
    
    public void pass(final CompileContext context) {
        if (context == null) return;
        switch (mode) {
            case METHOD -> context.availableMethods.add(new PreMethod(this));
            case FIELD -> context.availableFields.add(new PreField(this));
            case TYPE, STRUCTURE -> {
                context.availableTypes.add(this.owner);
                try {
                    final Class<?> cls = Class.forName(this.owner.dotPath());
                    context.importJava(cls);
                } catch (ClassNotFoundException ignore) {
                }
            }
        }
    }
    
    public void export(final CompileContext context) {
        if (context == null) return;
        switch (mode) {
            case METHOD -> context.exports.add(new PreMethod(this));
            case FIELD -> context.exports.add(new PreField(this));
            case TYPE, STRUCTURE -> context.exports.add(this.owner);
        }
    }
    
    public enum Mode {
        TYPE,
        METHOD,
        FIELD,
        STRUCTURE;
        
        public static Mode getType(final String value) {
            if (TYPE_PATTERN.matcher(value).matches())
                return TYPE;
            if (value.contains("::") && METHOD_PATTERN.matcher(value).matches())
                return METHOD;
            if (value.contains(".") && FIELD_PATTERN.matcher(value).matches())
                return FIELD;
            if (value.startsWith("S(") && STRUCTURE_PATTERN.matcher(value).matches())
                return STRUCTURE;
            throw new IllegalArgumentException("Unrecognised signature: '" + value + "'");
        }
    }
}
