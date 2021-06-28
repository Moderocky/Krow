package krow.test;

import krow.compiler.BasicCompiler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CompilationTest {
    
    @BeforeClass
    public static void first() {
        System.setProperty("TEST_STATE", "1");
    }
    
    @AfterClass
    public static void last() {
        System.clearProperty("TEST_STATE");
    }
    
    @Test
    public void basic() {
        final Class<?> basic = new BasicCompiler().compileAndLoad("""
            import <java/lang/String>
            // export <>
            class mx/kenzie/Example {
            }
            """);
        assert basic != null;
        assert basic.getName().equals("mx.kenzie.Example");
        assert !Modifier.isPublic(basic.getModifiers());
        assert !Modifier.isPrivate(basic.getModifiers());
        
    }
    
    @Test
    public void abstractMethod() throws Throwable {
        final Class<?> basic = new BasicCompiler().compileAndLoad("""
            import <java/lang/String>
            // export <>
            class mx/kenzie/Example2 {
                import <java/lang/Object>
                export <>
                abstract void testMethod();
            }
            """);
        assert basic != null;
        assert basic.getName().equals("mx.kenzie.Example2");
        assert !Modifier.isPublic(basic.getModifiers());
        assert !Modifier.isPrivate(basic.getModifiers());
        final Method method = basic.getMethod("testMethod");
        assert method != null;
        assert Modifier.isPublic(method.getModifiers());
        
    }
    
    @Test
    public void bodyMethod() throws Throwable {
        final Class<?> basic = new BasicCompiler().compileAndLoad("""
            import <java/lang/String>
            // export <>
            class mx/kenzie/Example3 {
                import <java/lang/Object>
                final void testMethod(Object var1, String var2) {
                }
            }
            """);
        assert basic != null;
        assert basic.getName().equals("mx.kenzie.Example3");
        assert !Modifier.isPublic(basic.getModifiers());
        assert !Modifier.isPrivate(basic.getModifiers());
        final Method method = basic.getDeclaredMethod("testMethod", Object.class, String.class);
        assert method != null;
        assert !Modifier.isPublic(method.getModifiers());
        
    }
    
    @Test
    public void bodyMethodInstruction() throws Throwable {
        final String source = """
            import <java/lang/String>
            export <>
            final class mx/kenzie/Fancy {
                import <java/io/PrintStream, PrintStream::println(String)V>
                export <>
                static final void testMethod(PrintStream out, String value) {
                    out.println(value);
                }
            }
            """;
        final Class<?> basic = new BasicCompiler().compileAndLoad(source);
        assert basic != null;
        assert basic.getName().equals("mx.kenzie.Fancy");
        final Method method = basic.getDeclaredMethod("testMethod", PrintStream.class, String.class);
        assert method != null;
        method.invoke(null, System.out, "hello");
    }
    
    @Test
    public void varAssign() throws Throwable {
        final String source = """
            import <
                java/lang/String,
                java/io/PrintStream,
                PrintStream::println(String)V
            >
            export <>
            final class mx/kenzie/Thingy {
            
                export <>
                static final void testMethod1(PrintStream out) {
                    String value = "hello there";
                    out.println(value);
                }
                
                export <>
                static final void testMethod2(PrintStream out) {
                    String value;
                    value = "general kenobi";
                    out.println(value);
                    out.println("you are a bold one :)");
                }
                
                export <>
                bridge <Thingy::testMethod2(PrintStream)V>
                static void bridgeMethod(Object object);
            }
            """;
        final Class<?> basic = new BasicCompiler().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("testMethod1", PrintStream.class).invoke(null, System.out);
        basic.getMethod("testMethod2", PrintStream.class).invoke(null, System.out);
    }
    
    @Test
    public void bridge() throws Throwable {
        final String source = """
            import <
                java/lang/String,
                java/io/PrintStream,
                PrintStream::println(String)V
            >
            export <>
            class mx/kenzie/Bridge {
            
                export <>
                static final void testMethod1(PrintStream out, String value) {
                    out.println(value);
                }
                
                export <>
                bridge <Bridge::testMethod1(PrintStream,String)V>
                static void bridgeMethod(PrintStream out, Object value);
            }
            """;
        final Class<?> basic = new BasicCompiler().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("testMethod1", PrintStream.class, String.class).invoke(null, System.out, "a");
        basic.getMethod("bridgeMethod", PrintStream.class, Object.class).invoke(null, System.out, "b");
    }
    
}
