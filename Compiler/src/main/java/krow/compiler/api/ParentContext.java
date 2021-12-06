package krow.compiler.api;

import krow.compiler.pre.PreField;
import krow.compiler.pre.PreMethod;
import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;

import java.util.Collection;

public interface ParentContext {
    
    boolean isInterface();
    boolean isClass();
    
    ClassBuilder getClassBuilder();
    
    ChildContext child();
    
    Collection<Type> getAvailableTypes();
    Collection<PreMethod> getAvailableMethods();
    Collection<PreField> getAvailableFields();
    
}
