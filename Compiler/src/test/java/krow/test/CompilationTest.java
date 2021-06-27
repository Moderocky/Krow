package krow.test;

import krow.compiler.BasicCompiler;
import krow.compiler.handler.inclass.MethodStartHandler;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class CompilationTest {
    
    @Test
    public void basic() {
        System.setProperty("TEST_STATE", "1");
        final Class<?> basic = new BasicCompiler().compileAndLoad("""
            import <java/lang/String>
            // export <>
            class mx/kenzie/Example {
            }
            """);
        System.clearProperty("TEST_STATE");
        assert basic != null;
        assert basic.getName().equals("mx.kenzie.Example");
        assert !Modifier.isPublic(basic.getModifiers());
        assert !Modifier.isPrivate(basic.getModifiers());
        
    }
    
    @Test
    public void abstractMethod() throws Throwable {
        System.setProperty("TEST_STATE", "1");
        final Class<?> basic = new BasicCompiler().compileAndLoad("""
            import <java/lang/String>
            // export <>
            class mx/kenzie/Example2 {
                import <java/lang/Object>
                export <>
                abstract void testMethod();
            }
            """);
        System.clearProperty("TEST_STATE");
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
        System.setProperty("TEST_STATE", "1");
        final Class<?> basic = new BasicCompiler().compileAndLoad("""
            import <java/lang/String>
            // export <>
            class mx/kenzie/Example3 {
                import <java/lang/Object>
                final void testMethod(Object var1, String var2) {
                }
            }
            """);
        System.clearProperty("TEST_STATE");
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
        System.setProperty("TEST_STATE", "1");
        final String source = """
            import <java/lang/String>
            export <>
            class mx/kenzie/Fancy {
                import <java/io/PrintStream, PrintStream::println(String)V>
                export <>
                static
                final void testMethod(PrintStream out, String value) {
                    out.println(value);
                }
            }
            """;
        final Class<?> basic = new BasicCompiler().compileAndLoad(source);
        final byte[] bytes = new BasicCompiler().compile(source);
        new FileOutputStream("blob.class").write(bytes);
        System.clearProperty("TEST_STATE");
        assert basic != null;
        assert basic.getName().equals("mx.kenzie.Fancy");
        final Method method = basic.getDeclaredMethod("testMethod", PrintStream.class, String.class);
        assert method != null;
        method.invoke(null, System.out, "hello");
    }
    
}
