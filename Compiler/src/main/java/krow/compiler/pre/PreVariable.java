package krow.compiler.pre;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.Objects;

public class PreVariable {
    public String name;
    public Type type;
    
    public PreVariable(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    
    public WriteInstruction load(int slot) {
        return switch (type.dotPath()) {
            case "char", "boolean", "byte", "int", "short", "void" -> WriteInstruction.loadSmall(slot);
            case "long" -> WriteInstruction.loadLong(slot);
            case "float" -> WriteInstruction.loadFloat(slot);
            case "double" -> WriteInstruction.loadDouble(slot);
            default -> WriteInstruction.loadObject(slot);
        };
    }
    
    public WriteInstruction store(int slot) {
        return switch (type.dotPath()) {
            case "char", "boolean", "byte", "int", "short", "void" -> WriteInstruction.storeSmall(slot);
            case "long" -> WriteInstruction.storeLong(slot);
            case "float" -> WriteInstruction.storeFloat(slot);
            case "double" -> WriteInstruction.storeDouble(slot);
            default -> WriteInstruction.storeObject(slot);
        };
        
    }
    
    public String name() {
        return name;
    }
    
    public Type type() {
        return type;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PreVariable) obj;
        return Objects.equals(this.name, that.name) &&
            Objects.equals(this.type, that.type);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
    
    @Override
    public String toString() {
        return "PreVariable[" +
            "name=" + name + ", " +
            "type=" + type + ']';
    }
    
    
}
