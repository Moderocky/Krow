package krow.compiler.pre;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

import java.util.Objects;

public class PreVariable {
    public final String name;
    public Type type;
    
    public PreVariable(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    
    public static WriteInstruction load(final Type type, final int slot) {
        if (type.isArray()) {
            if (type.isPrimitiveArray()) {
                char c = type.descriptor().charAt(type.descriptor().length()-1);
                return switch (c) {
                    case 'L' -> WriteInstruction.arrayLoad(long.class);
                    case 'F' -> WriteInstruction.arrayLoad(float.class);
                    case 'D' -> WriteInstruction.arrayLoad(double.class);
                    default -> WriteInstruction.arrayLoad(int.class);
                };
            } else {
                return WriteInstruction.arrayLoadObject();
            }
        }
        return switch (type.dotPath()) {
            case "char", "boolean", "byte", "int", "short", "void" -> WriteInstruction.loadSmall(slot);
            case "long" -> WriteInstruction.loadLong(slot);
            case "float" -> WriteInstruction.loadFloat(slot);
            case "double" -> WriteInstruction.loadDouble(slot);
            default -> WriteInstruction.loadObject(slot);
        };
    }
    
    public static WriteInstruction store(final Type type, final int slot) {
        if (type.isArray()) {
            if (type.isPrimitiveArray()) {
                char c = type.descriptor().charAt(type.descriptor().length()-1);
                return switch (c) {
                    case 'L' -> WriteInstruction.arrayStore(long.class);
                    case 'F' -> WriteInstruction.arrayStore(float.class);
                    case 'D' -> WriteInstruction.arrayStore(double.class);
                    default -> WriteInstruction.arrayStore(int.class);
                };
            } else {
                return WriteInstruction.arrayStoreObject();
            }
        }
        return switch (type.dotPath()) {
            case "char", "boolean", "byte", "int", "short", "void" -> WriteInstruction.storeSmall(slot);
            case "long" -> WriteInstruction.storeLong(slot);
            case "float" -> WriteInstruction.storeFloat(slot);
            case "double" -> WriteInstruction.storeDouble(slot);
            default -> WriteInstruction.storeObject(slot);
        };
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
    public int hashCode() {
        return Objects.hash(name, type);
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
    public String toString() {
        return "PreVariable[" +
            "name=" + name + ", " +
            "type=" + type + ']';
    }
    
    
}
