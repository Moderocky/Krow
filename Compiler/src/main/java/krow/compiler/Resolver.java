package krow.compiler;

import krow.compiler.pre.PreStructure;
import mx.kenzie.foundation.Type;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Resolver {
    
    public static Type resolveType(final String name, final Type... types) {
        if (name.contains("[")) {
            Type end = resolveType(name.substring(0, name.indexOf("[")), types);
            if (end == null) return null;
            String input = name;
            int x;
            while ((x = input.indexOf("[")) > -1) {
                input = input.substring(x + 1); // count array dimensions
                end = end.arrayType(); // stack array dimensions
            }
            return end;
        }
        if (name.contains("/"))
            return Type.of(name);
        switch (name) {
            case "B":
            case "byte":
                return new Type(byte.class);
            case "S":
            case "short":
                return new Type(short.class);
            case "I":
            case "int":
                return new Type(int.class);
            case "J":
            case "long":
                return new Type(long.class);
            case "F":
            case "float":
                return new Type(float.class);
            case "D":
            case "double":
                return new Type(double.class);
            case "C":
            case "char":
                return new Type(char.class);
            case "Z":
            case "boolean":
                return new Type(boolean.class);
            case "V":
            case "void":
                return new Type(void.class);
            case "struct":
                return new Type(Object.class);
        }
        for (final Type type : types) {
            if (!type.internalName().endsWith(name)) continue;
            if (type.getSimpleName().equals(name)) return type;
        }
        return null;
        // throw new RuntimeException("Unable to resolve type: '" + name + "'");
    }
    
    public static Type resolveStructureType(final String[] names, final String[] extracted, final Type... types) {
        final Type[] fields = new Type[extracted.length];
        for (int i = 0; i < extracted.length; i++) {
            fields[i] = resolveType(extracted[i], types);
        }
        return new Type(resolveStructurePath(new PreStructure(names, fields)));
    }
    
    public static Type resolveStructureType(final PreStructure structure) {
        return new Type(resolveStructurePath(structure));
    }
    
    public static Type resolveStructureType(final String signature, final Type... types) {
        return new Type(resolveStructurePath(resolveStructure(signature, types)));
    }
    
    private record Result(String remainder, PreStructure type) {
    
    }
    
    private static Result resolveStructure0(final String signature, final Type... types) {
        final String inner = signature.substring(2);
        final Type[] fieldTypes;
        final String[] fieldNames;
        String remainder = "";
        if (inner.contains("S(") || inner.contains("),")) {
            final List<String> names = new ArrayList<>();
            final List<Type> fields = new ArrayList<>();
            String current = inner;
            while (!current.isEmpty() && current.indexOf(':') > 0) {
                if (current.startsWith(")")) break;
                final int split = current.indexOf(':');
                final int end;
                final String name = current.substring(0, split).trim();
                final Type type;
                names.add(name);
                remainder = current.substring(split + 1).trim();
                if (remainder.startsWith("S(")) {
                    final Result result = resolveStructure0(remainder, types);
                    fields.add(type = resolveStructureType(result.type));
                    current = result.remainder;
                    if (current.startsWith(",")) current = current.substring(1);
                } else {
                    int x = remainder.indexOf(',');
                    int y = remainder.indexOf(')');
                    end = (x > 0 && x < y) ? x : y;
                    fields.add(type = resolveType(remainder.substring(0, end), types));
                    current = remainder.substring((x > 0 && x < y) ? end + 1 : end).trim();
                }
            }
            fieldTypes = fields.toArray(new Type[0]);
            fieldNames = names.toArray(new String[0]);
            if (current.startsWith(")"))
                remainder = current.substring(1);
            else remainder = current;
        } else {
            final String[] parts = inner.split(",");
            fieldTypes = new Type[parts.length];
            fieldNames = new String[parts.length];
            for (int i = 0; i < parts.length; i++) {
                final int end = parts[i].indexOf(')') > 0 ? parts[i].indexOf(')') : parts[i].length();
                fieldNames[i] = parts[i].substring(0, parts[i].indexOf(':'));
                fieldTypes[i] = resolveType(parts[i].substring(parts[i].indexOf(':') + 1, end), types);
            }
        }
        return new Result(remainder, new PreStructure(fieldNames, fieldTypes));
    }
    
    public static PreStructure resolveStructure(final String signature, final Type... types) {
        final Result result = resolveStructure0(signature, types);
        return result.type;
    }
    
    public static String resolveStructurePath(final PreStructure structure) {
        final StringBuilder builder = new StringBuilder("krow.lang.Structure$");
        for (final String s : structure.fields.keySet()) {
            builder.append(s.charAt(0));
        }
        final int label = builder.toString().hashCode();
        final List<Object> list = new ArrayList<>(structure.fields.values());
        final int weak = Objects.hash(structure.fields.keySet());
        final int first = Objects.hash(list);
        Collections.reverse(list);
        final int second = Objects.hash(list);
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(0, first);
        buffer.putInt(4, second);
        final long strong = buffer.getLong();
        return "krow.lang.Structure$L" + Math.abs(label) + "W" + Math.abs(weak) + "S" + Math.abs(strong);
    }
    
}
