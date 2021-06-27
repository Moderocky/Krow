package krow.compiler.pre;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;

public record PreVariable(String name, Type type) {
    
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
    
}
