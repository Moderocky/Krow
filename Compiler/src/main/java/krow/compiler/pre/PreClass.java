package krow.compiler.pre;

import mx.kenzie.foundation.Type;

import java.util.ArrayList;
import java.util.List;

public class PreClass extends PreContext {
    
    public final List<Type> interfaces = new ArrayList<>();
    public Type path;
    public Type extend = new Type(Object.class);
    
    
}
