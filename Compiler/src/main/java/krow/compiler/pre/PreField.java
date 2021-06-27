package krow.compiler.pre;

import mx.kenzie.foundation.Type;

public class PreField {
    
    public Type owner;
    public Type type;
    public String name;
    
    public PreField(Type owner, Type type, String name) {
        this.owner = owner;
        this.type = type;
        this.name = name;
    }
    
    public PreField(final Signature signature) {
        owner = signature.owner;
        type = signature.bound;
        name = signature.name;
    }
    
    public Signature getSignature() {
        final Signature signature = new Signature();
        signature.bound = type;
        signature.owner = owner;
        signature.name = name;
        signature.mode = Signature.Mode.FIELD;
        return signature;
    }
    
}
