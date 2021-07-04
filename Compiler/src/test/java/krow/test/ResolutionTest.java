package krow.test;

import krow.compiler.Resolver;
import krow.compiler.pre.PreStructure;
import krow.compiler.pre.Signature;
import krow.compiler.util.SourceReader;
import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.junit.Test;

import java.lang.reflect.Modifier;
import java.util.List;

public class ResolutionTest {
    
    public static void test() {
        System.out.println("hello");
    }
    
    @Test
    public void types() {
        Resolver.resolveType("Object", new Type(Object.class));
    }
    
    @Test
    public void hashing() {
        assert "".hashCode() == 0;
        assert "a".hashCode() == 97;
        assert "abc".hashCode() == 96354;
        assert "cba".hashCode() == 98274;
        assert "aaa".hashCode() == 96321;
        final PreStructure structure = new PreStructure();
        structure.fields.put("name", new Type(String.class));
        structure.fields.put("age", new Type(int.class));
        structure.fields.put("stage", new Type(Object.class));
        assert "krow.lang.Structure$L1265572110W113227431S2852571371512584461"
            .equals(Resolver.resolveStructurePath(structure));
    }
    
    @Test
    public void signatures() {
        assert Signature.Mode.getType("org/example/MyClass") == Signature.Mode.TYPE;
        assert Signature.Mode.getType("MyClass$Nested") == Signature.Mode.TYPE;
        assert Signature.Mode.getType("org/exa_mple/MyClass$Nested") == Signature.Mode.TYPE;
        assert Signature.Mode.getType("this::myMethod()V") == Signature.Mode.METHOD;
        assert Signature.Mode.getType("org/example/Class::myMethod()V") == Signature.Mode.METHOD;
        assert Signature.Mode.getType("Class::myMethod(java/lang/String,I)V") == Signature.Mode.METHOD;
        assert Signature.Mode.getType("org/example/Class::myMethod(java/lang/String,I)V") == Signature.Mode.METHOD;
        assert Signature.Mode.getType("org/example/Class::my_Method0(java/lang/String,I)java/lang/Void") == Signature.Mode.METHOD;
        assert Signature.Mode.getType("S(name:java/lang/String,age:I)") == Signature.Mode.STRUCTURE;
        assert Signature.Mode.getType("S(fieldName:org/example/Blob,f_n0:Long)") == Signature.Mode.STRUCTURE;
        {
            final PreStructure structure = Resolver.resolveStructure("S(name:String,age:I)", new Type(String.class));
            assert structure.fields.size() == 2;
        }
        {
            final PreStructure structure = Resolver.resolveStructure("S(name:String,age:I,structure:S(name:String))", new Type(String.class));
            assert structure.toString()
                .equals("S(name:java/lang/String,age:int,structure:krow/lang/Structure$L901475364W3373738S1611797601888125282)");
        }
        {
            final PreStructure structure = Resolver.resolveStructure("S(name:String,age:I,structure:S(name:String,structure:S(name:String)))", new Type(String.class));
            assert structure.toString()
                .equals("S(name:java/lang/String,age:int,structure:krow/lang/Structure$L2119034903W147892253S3780060826278253525)");
        }
        {
            final PreStructure structure = Resolver.resolveStructure("S(name0:String,age:I,sub1:S(name1:String,sub2:S(name2:String)),end:V)", new Type(String.class));
            assert structure.toString()
                .equals("S(name0:java/lang/String,age:int,sub1:krow/lang/Structure$L2119034903W108126487S3913422602618471041,end:void)");
        }
    }
    
    @Test
    public void equality() {
        assert Object.class.descriptorString().equals(new Type(Object.class).descriptorString());
        assert org.objectweb.asm.Type.getInternalName(String.class).equals(new Type(String.class).internalName());
        assert int.class.descriptorString().equals(new Type(int.class).descriptorString());
    }
    
    @Test
    public void source() throws Throwable {
        final List<WriteInstruction> list = SourceReader.getSource(ResolutionTest.class.getMethod("test"));
        final Class<?> cls = new ClassBuilder("Blob").addModifiers(Modifier.PUBLIC)
            .addMethod("test").addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            .writeCode(list.toArray(new WriteInstruction[0])).finish().compileAndLoad();
        assert cls != null;
        cls.getMethod("test").invoke(null);
    }
    
}
