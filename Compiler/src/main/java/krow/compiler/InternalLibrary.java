package krow.compiler;

import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.language.PostCompileClass;

import java.io.InputStream;

public interface InternalLibrary {
    static PostCompileClass getRuntimeDependency(String dotPath, String path) {
        try (final InputStream stream = SystemLibrary.class.getClassLoader().getResourceAsStream(path)) {
            assert stream != null;
            final byte[] bytecode = stream.readAllBytes();
            return new PostCompileClass(bytecode, dotPath, new Type(dotPath).internalName());
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
}
