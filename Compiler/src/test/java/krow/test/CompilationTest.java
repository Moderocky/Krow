package krow.test;

import krow.compiler.ReKrow;
import krow.compiler.Krow;
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
        final Class<?> basic = new ReKrow().compileAndLoad("""
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
        final Class<?> basic = new ReKrow().compileAndLoad("""
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
        final Class<?> basic = new ReKrow().compileAndLoad("""
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
        final Class<?> basic = new ReKrow().compileAndLoad(source);
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
                
                static String internalMethod() {
                    return "you are a bold one :)";
                }
                
                export <>
                static final void testMethod2(PrintStream out) {
                    String value;
                    value = "general kenobi";
                    String end = Thingy
                        .internalMethod();
                    out.println(value);
                    out
                        .println(
                            end
                        );
                }
                
                export <>
                bridge <Thingy::testMethod2(PrintStream)V>
                static void bridgeMethod(Object object);
            }
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
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
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("testMethod1", PrintStream.class, String.class).invoke(null, System.out, "a");
        basic.getMethod("bridgeMethod", PrintStream.class, Object.class).invoke(null, System.out, "b");
    }
    
    @Test
    public void constructor() throws Throwable {
        final String source = """
            import <java/lang/String, java/lang/System, java/io/PrintStream, System.out:PrintStream, PrintStream::println(String)V>
            export <>
            class mx/kenzie/Const {
                
                export <>
                void <init>(String name) {
                    super();
                    System.out.println(name);
                }
                
                export <>
                void testMethod1(PrintStream out, String value) {
                    out.println(value);
                }
                
            }
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        final Object object = basic.getConstructor(String.class).newInstance("hello");
        assert object != null;
        basic.getMethod("testMethod1", PrintStream.class, String.class).invoke(object, System.out, "there");
    }
    
    @Test
    public void arrays() throws Throwable {
        final String source = """
            import <java/io/PrintStream, java/util/Arrays>
            export <>
            class mx/kenzie/Arr {
                
                import <Arrays::toString(Object[])String>
                export <>
                static void main(String[] value) {
                    System.out.println(Arrays.toString(value));
                }
                
            }
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("main", String[].class).invoke(null, (Object) new String[]{"hello", "there"});
    }
    
    @Test
    public void allocation() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Alloc {
                
                void <init>(String name) {
                    System.out.println(name);
                    super();
                }
                
                export <>
                static void test() {
                    Alloc var1 = new Alloc;
                    var1("hello");
                    Alloc var2 = new Alloc("there");
                }
                
            }
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void dynamic() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/example/Dynamic {
                
                void <init>(String name) {
                    System.out.println(name);
                    super();
                }
                
                import <Dynamic::myMethod(String)V, krow/test/AssumptionTest, AssumptionTest::privateMethod()Z>
                export <>
                static void test() {
                    Dynamic var1 = new Dynamic;
                    Dynamic var2 = new Dynamic("there");
                    var1("hello");
                    var1#myMethod("thing");
                    
                    boolean boo = AssumptionTest#privateMethod();
                    System.out.println(boo);
                }
                
                void myMethod(String name) {
                    System.out.println(name);
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void smalls() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Small {
                
                static void myMethod(int i) {
                    System.out.println(i);
                }
                
                export <>
                static void test() {
                    int i = 20;
                    Small.myMethod(i);
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void consts() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Constants {
                
                const a = "hello";
                
                export <>
                static void test() {
                    const b = "there";
                    System.out.println(a);
                    System.out.println(b);
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void structs() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Blob6 {
                
                const name = "hello";
                
                static struct myMethod() {
                    int age = 66;
                    return struct(name, age);
                }
                
                export <>
                static void test() {
                    struct data = Blob6.myMethod();
                    System.out.println(data.name);
                    System.out.println(data.age);
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void maths() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Maths {
                
                export <>
                static void test() {
                    int a = 1 + 1;
                    int b = (1 + 3);
                    int c = 2 + (3 * 4);
                    double d = 2D - (3D / 4D);
                    System.out.println(a);
                    System.out.println(b);
                    System.out.println(c);
                    System.out.println(d);
                    System.out.println(((2 + 3 )* 4));
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void jump() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Jump {
                
                export <>
                static void test() {
                    int a = 1 + 1;
                    int b = (1 + 3);
                    int c = 2 + (3 * 4);
                    goto test;
                    double d = 2D - (3D / 4D);
                    System.out.println(d);
                    System.out.println(a);
                    System.out.println(b);
                    label test;
                    System.out.println(c);
                    System.out.println(((2 + 3 )* 4));
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void def() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Default {
                
                export <>
                static void test() {
                    String var1 = null;
                    String var2 = "hello";
                    String result = (var1 ? var2);
                    System.out.println(result);
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void eq() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Equals {
                
                export <>
                static void test() {
                    String var1 = null;
                    String var2 = "hello";
                    boolean result = var1 == var2;
                    boolean blob = var1 != var2;
                    System.out.println(result);
                    System.out.println(blob);
                    System.out.println((1 == 1));
                    System.out.println((1 != 2));
                    boolean a = 1 < 2;
                    boolean b = 2 < 2;
                    boolean c = 2 <= 2;
                    boolean d = 3 > 2;
                    boolean e = 2 > 2;
                    boolean f = 2 >= 2;
                    System.out.println(a);
                    System.out.println(b);
                    System.out.println(c);
                    System.out.println(d);
                    System.out.println(e);
                    System.out.println(f);
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void iff() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Iff {
                
                export <>
                static void test() {
                    String var1 = null;
                    String var2 = "hello";
                    if (var1 != var2) System.out.println(var2);
                    if (var1 == var2) System.out.println("blob");
                    System.out.println("there");
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void logic() throws Throwable {
        final String source = """
            import <java/io/PrintStream> export <>
            class mx/kenzie/Logic {
                
                export <>
                static void test() {
                    boolean a = false;
                    boolean b = !a;
                    boolean c = !b;
                    assert b;
                    assert !c;
                    assert 1 == 1;
                    assert (-101 == ~100);
                    assert true;
                    assert !a;
                    assert true & true;
                    assert true | false;
                    assert ~false;
                    assert !false;
                    
                }
                
            }
            
            """;
        final Class<?> basic = new ReKrow().compileAndLoad(source);
        assert basic != null;
        basic.getMethod("test").invoke(null);
    }
    
    @Test
    public void full() {
        Krow.main("TestTarget.ark", "src/test/krow", "mx.kenzie.example.Main");
    }
    
    private void debug(final String source) throws Throwable {
        new FileOutputStream("debug.class").write(new ReKrow().compile(source));
    }
    
}
