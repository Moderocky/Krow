package krow.test;

import krow.compiler.ReKrow;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SystemLibraries {
    
    private ReKrow krow;
    
    @Test
    public void install() {
        System.setProperty("TEST_STATE", "2");
        krow = new ReKrow();
        compile("Runtime.class", "lib/runtime.kro");
        compile("Structure.class", "lib/struct.kro");
        compile("Overseer.class", "lib/unsafe.kro");
        System.setProperty("TEST_STATE", "1");
        System.clearProperty("TEST_STATE");
    }
    
    private void compile(final String result, final String path) {
        final File file = new File("src/main/resources/" + result);
        final File source = new File("src/main/resources/" + path);
        try (final InputStream stream = new FileInputStream(source);
             final FileOutputStream output = new FileOutputStream(file)) {
            final byte[] bytecode = krow.compile(stream)[0].code();
            output.write(bytecode);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
