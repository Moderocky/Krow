package krow.memory;

import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class OverseerTemplate {
    
    public static long getMemorySize(Class<?> cls, Unsafe unsafe) {
        if (cls == byte.class) return (1);
        if (cls == short.class) return (2);
        if (cls == int.class) return (4);
        if (cls == long.class) return (8);
        if (cls == boolean.class) return (1);
        if (cls == char.class) return (2);
        if (cls == float.class) return (4);
        if (cls == double.class) return (8);
        if (cls == void.class) return (1);
        final Set<Field> fields = new HashSet<>();
        while (cls != Object.class) {
            for (final Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                fields.add(field);
            }
            cls = cls.getSuperclass();
        }
        long maxSize = 0;
        for (final Field field : fields) {
            final long offset = unsafe.objectFieldOffset(field);
            if (offset > maxSize) maxSize = offset;
        }
        return ((maxSize / 8) + 1) * 8;
    }
    
    public static long getAddress(Object object, Unsafe unsafe) {
        final Object[] objects = new Object[]{object};
        final int offset = unsafe.arrayBaseOffset(objects.getClass());
        final int scale = unsafe.arrayIndexScale(objects.getClass());
        return switch (scale) {
            case 4 -> (unsafe.getInt(objects, offset) & 0xFFFFFFFFL) * 8;
//            case 8 -> // TODO: 09/11/2020 Add impl for 8-scaled arrays?
            default -> throw new IllegalStateException("Unexpected value: " + scale);
        };
    }
    
    public static Object allocateInstance(Class<?> cls, Unsafe unsafe) {
        try {
            return unsafe.allocateInstance(cls);
        } catch (InstantiationException ex) {
            if (cls.isArray()) {
                return Array.newInstance(cls.getComponentType(), 0);
            } else if (cls.isEnum()) {
                return cls.getEnumConstants()[0];
            } else if (cls.isPrimitive()) {
                return null;
            }
            throw new RuntimeException(ex);
        }
    }
    
}
