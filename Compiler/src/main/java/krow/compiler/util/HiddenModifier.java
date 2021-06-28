package krow.compiler.util;

public class HiddenModifier {
    
    public static final int BRIDGE = 0x00000040;
    public static final int VARARGS = 0x00000080;
    public static final int SYNTHETIC = 0x00001000;
    public static final int ANNOTATION = 0x00002000;
    public static final int ENUM = 0x00004000;
    public static final int MANDATED = 0x00008000;
    
    public static boolean isSynthetic(int mod) {
        return (mod & SYNTHETIC) != 0;
    }
    
    public static boolean isBridge(int mod) {
        return (mod & BRIDGE) != 0;
    }
    
    public static boolean isVarArgs(int mod) {
        return (mod & VARARGS) != 0;
    }
    
    public static boolean isMandated(int mod) {
        return (mod & MANDATED) != 0;
    }
    
}
