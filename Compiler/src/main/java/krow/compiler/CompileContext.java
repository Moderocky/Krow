package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.Library;
import krow.compiler.pre.*;
import mx.kenzie.foundation.*;
import mx.kenzie.foundation.language.PostCompileClass;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("ALL")
public class CompileContext extends WorkContext {
    
    public final List<Type> availableTypes = new ArrayList<>();
    public final List<PreMethod> availableMethods = new ArrayList<>();
    public final List<PreField> availableFields = new ArrayList<>();
    public final List<PostCompileClass> attachments = new ArrayList<>();
    public final Map<String, Object> constants = new HashMap<>();
    public CompileContext child;
    public CompileState superState;
    public HandlerSet handlers;
    public boolean isInterface;
    public boolean isClass;
    public int modifiersUpcoming;
    public PreMethod bridgeTarget;
    public ClassBuilder builder;
    public FieldBuilder currentField;
    public MethodBuilder currentMethod;
    public PreMethod method;
    protected KrowCompiler compiler;

    {
        importJava(java.lang.String.class);
        importJava(java.lang.Object.class);
        importJava(java.lang.Integer.class);
        importJava(java.lang.Class.class);
        importJava(java.lang.System.class);
        importJava(java.lang.Integer.class);
        importJava(java.lang.Character.class);
        importJava(java.lang.Boolean.class);
        importJava(java.lang.Byte.class);
        importJava(java.lang.Short.class);
        importJava(java.lang.Long.class);
        importJava(java.lang.Integer.class);
        importJava(java.lang.Float.class);
        importJava(java.lang.Double.class);
        importJava(java.lang.Void.class);
        importJava(java.lang.StringBuilder.class);
        importJava(java.lang.RuntimeException.class);
        importJava(java.lang.Runtime.class);
        importJava(java.lang.Runnable.class);
        importJava(java.lang.Error.class);
        importJava(java.lang.Throwable.class);
        importJava(java.lang.Exception.class);
        importJava(java.lang.Number.class);
    }
    
    public void createChild() {
        this.child = new CompileContext();
        this.child.handlers = handlers;
    }
    
    public void addCompileTimeLibrary(final String identifier) {
        Library library = null;
        for (final Library check : compiler.getLibraries()) {
            if (!check.identifier().equals(identifier)) continue;
            library = check;
            break;
        }
        if (library == null) throw new RuntimeException("Library '" + identifier + "' not registered.");
        for (final CompileState state : handlers.keySet()) {
            handlers.get(state).addAll(library.getHandlers(state));
        }
        attachments.addAll(library.getRuntime());
    }
    
    public List<PreLabel> labels() {
        return child != null ? child.labels() : labels;
    }
    
    public PreLabel label(final String name) {
        if (child != null) return child.label(name);
        for (final PreLabel label : labels()) {
            if (label.name.equals(name)) return label;
        }
        final PreLabel label = new PreLabel();
        label.name = name;
        labels.add(label);
        return label;
    }
    
    public List<PreBracket> brackets() {
        return child != null ? child.brackets() : brackets;
    }
    
    @Override
    public WorkContext child() {
        return child;
    }
    
    public List<WriteInstruction> statement() {
        if (brackets.isEmpty())
            return child == null ? statement : child.statement;
        else return brackets.get(0).statement();
    }
    
    public void importJava(final Class<?> cls) {
        final Type type = new Type(cls);
        availableTypes.add(type);
        for (final Method method : cls.getMethods()) {
            availableMethods.add(new PreMethod(method));
        }
        for (final Field field : cls.getFields()) {
            availableFields.add(new PreField(field));
        }
    }
    
    public boolean upcoming(int modifiers) {
        final int test;
        if (child != null) test = child.modifiersUpcoming; // preference for child modifiers
        else test = modifiersUpcoming;
        return (test & modifiers) != 0;
    }
    
    public void addUpcoming(int modifiers) { // want to make sure they go on child so we can trough after dead end
        if (child != null) child.modifiersUpcoming |= modifiers;
        else modifiersUpcoming |= modifiers;
    }
    
    public void clearUpcoming() {
        if (child != null) child.modifiersUpcoming = 0;
        else modifiersUpcoming = 0;
    }
    
    public int upcoming() {
        final int test;
        if (child != null) test = child.modifiersUpcoming;
        else test = modifiersUpcoming;
        return test;
    }
    
    public int getSlot(final PreVariable variable) {
        if (child != null) return child.variables.indexOf(variable);
        return variables.indexOf(variable);
    }
    
    public int getSlot(final String variable) {
        if (child != null) return child.getSlot(variable);
        for (int i = 0; i < variables.size(); i++) {
            if (variables.get(i).name().equals(variable)) return i;
        }
        return -1;
    }
    
    public PreMethod findMethod(final Signature signature) {
        final List<PreMethod> methods = availableMethods();
        final PreMethod test = new PreMethod(signature);
        for (final PreMethod method : methods) {
            if (method.name.equals(test.name) && method.owner.equals(test.owner) && method.parameters.equals(test.parameters))
                return method;
        }
        return null;
    }
    
    public PreField findField(final Type owner, final String name) {
        final List<PreField> fields = availableFields();
        for (final PreField field : fields) {
            if (field.name.equals(name) && field.owner.equals(owner)) return field;
        }
        return null;
    }
    
    public PreMethod findMethod(final Type owner, final String name) {
        final List<PreMethod> methods = availableMethods();
        for (final PreMethod method : methods) {
            if (method.name.equals(name) && method.owner.equals(owner)) return method;
        }
        return null;
    }
    
    public PreMethod findMethod(final Type owner, final String name, int params) {
        final List<PreMethod> methods = availableMethods();
        final String seenName;
        if (Objects.equals(name, "<init>")) seenName = "new";
        else seenName = name;
        for (final PreMethod method : methods) {
            if (method.name.equals(seenName) && method.owner.equals(owner) && method.parameters.size() == params)
                return method;
        }
        return null;
    }
    
    public PreMethod findMethod(final Type owner, final String name, final List<Type> parameters) {
        final List<PreMethod> methods = availableMethods();
        final String seenName;
        if (Objects.equals(name, "<init>")) seenName = "new";
        else seenName = name;
        for (final PreMethod method : methods) {
            if (method.name.equals(seenName) && method.owner.equals(owner) && parameters.equals(method.parameters))
                return method;
        }
        if (name.equals("<init>")) return getImplicitConstructor(owner, parameters);
        return null;
    }
    
    public PreMethod getImplicitConstructor(final Type owner, final List<Type> parameters) {
        final PreMethod method = new PreMethod();
        method.name = "<init>";
        method.returnType = new Type(void.class);
        method.owner = owner;
        method.parameters.addAll(parameters);
        return method;
    }
    
    public boolean hasVariable(final String name) {
        List<PreVariable> variables = child == null ? this.variables : this.child.variables;
        for (PreVariable preVariable : variables) {
            if (preVariable.name().equals(name)) return true;
        }
        return false;
    }
    
    public PreVariable getVariable(final String variable) {
        List<PreVariable> variables = child == null ? this.variables : this.child.variables;
        for (PreVariable preVariable : variables) {
            final PreVariable var;
            if ((var = preVariable).name().equals(variable)) return var;
        }
        return null;
    }
    
    public Type resolveType(final String name) {
        return Resolver.resolveType(name, availableTypes().toArray(new Type[0]));
    }
    
    public boolean isRoot() {
        return child != null;
    }
    
    public List<PreField> availableFields() {
        final List<PreField> fields = new ArrayList<>();
        if (child != null) fields.addAll(child.availableFields());
        fields.addAll(availableFields);
        return fields;
    }
    
    public List<PreMethod> availableMethods() {
        final List<PreMethod> methods = new ArrayList<>();
        if (child != null) methods.addAll(child.availableMethods());
        methods.addAll(availableMethods);
        return methods;
    }
    
    public List<Type> availableTypes() {
        final List<Type> types = new ArrayList<>();
        if (child != null) types.addAll(child.availableTypes());
        types.addAll(availableTypes);
        return types;
    }
    
    public Object getConstant(final String name) {
        if (constants.containsKey(name)) return constants.get(name);
        if (child != null) return child.getConstant(name);
        return null;
    }
    
    public boolean hasConstant(final String name) {
        if (child != null)
            return constants.containsKey(name) || child.hasConstant(name);
        return constants.containsKey(name);
    }
    
    public Map<String, Object> getConstants() {
        final Map<String, Object> map = new HashMap<>();
        if (child != null) map.putAll(child.constants);
        map.putAll(constants);
        return map;
    }
    
}
