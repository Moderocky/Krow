package krow.compiler;

import krow.compiler.pre.PreField;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.PreMethodCall;
import krow.compiler.pre.PreVariable;
import mx.kenzie.foundation.*;

import java.util.ArrayList;
import java.util.List;

public class CompileContext {
    
    public CompileContext child;
    
    public CompileState superState;
    
    public List<Type> availableTypes = new ArrayList<>();
    public List<PreMethod> availableMethods = new ArrayList<>();
    public List<PreField> availableFields = new ArrayList<>();
    
    // child
    public boolean isStatic;
    public List<PreVariable> variables = new ArrayList<>();
    public List<WriteInstruction> statement = new ArrayList<>();
    public List<PreMethodCall> preparing = new ArrayList<>();
    public CompileExpectation expectation = CompileExpectation.NONE;
    public PreVariable store;
    public boolean inverted;
    public List<Object> exports = new ArrayList<>();
    public boolean exported;
    public boolean hasBody;
    
    public ClassBuilder builder;
    public FieldBuilder currentField;
    public MethodBuilder currentMethod;
    public PreMethod method;
    
    public int getSlot(final String variable) {
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).name().equals(variable)) return i;
        }
        return -1;
    }
    
    public PreMethod findMethod(final Type owner, final String name) {
        final List<PreMethod> methods = new ArrayList<>(availableMethods);
        if (child != null) methods.addAll(child.availableMethods);
        for (final PreMethod method : methods) {
            if (method.name.equals(name) && method.owner.equals(owner)) return method;
        }
        return null;
    }
    
    public PreMethod findMethod(final Type owner, final String name, int params) {
        final List<PreMethod> methods = new ArrayList<>(availableMethods);
        if (child != null) methods.addAll(child.availableMethods);
        for (final PreMethod method : methods) {
            if (method.name.equals(name) && method.owner.equals(owner) && method.parameters.size() == params)
                return method;
        }
        return null;
    }
    
    public PreMethod findMethod(final Type owner, final String name, final List<Type> parameters) {
        final List<PreMethod> methods = new ArrayList<>(availableMethods);
        if (child != null) methods.addAll(child.availableMethods);
        for (final PreMethod method : methods) {
            if (method.name.equals(name) && method.owner.equals(owner) && parameters.equals(method.parameters))
                return method;
        }
        return null;
    }
    
    public PreVariable getVariable(final String variable) {
        List<PreVariable> variables = child == null ? this.variables : this.child.variables;
        for (int i = 0; i < variables.size(); i++) {
            final PreVariable var;
            if ((var = variables.get(i)).name().equals(variable)) return var;
        }
        return null;
    }
    
    public boolean isRoot() {
        return child != null;
    }
    
    public List<Type> availableTypes() {
        final List<Type> types = new ArrayList<>(availableTypes);
        if (child != null) types.addAll(child.availableTypes());
        return types;
    }
    
}
