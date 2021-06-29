package krow.compiler;

import mx.kenzie.foundation.language.LanguageDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Krow implements LanguageDefinition {
    
    @Override
    public String name() {
        return "Krow";
    }
    
    @Override
    public String sourceFileExt() {
        return "kro";
    }
    
    public static void main(String... args) {
        if (args.length > 1) {
            final File target = new File(args[0]);
            final File root = new File(args[1]);
            final String main = args.length > 2 ? args[2] : null;
            System.out.println("Compiling: '" + root + "'");
            if (!root.exists()) throw new IllegalArgumentException("Root file does not exist.");
            final List<File> files = getFiles(new ArrayList<>(), root.toPath());
//            files.removeIf(file -> !file.getName().endsWith(".kro"));
            new BasicCompiler().compileResource(main, target, files.toArray(new File[0]));
        } else {
            System.out.println("Correct arguments: output target & source root");
        }
    }
    
    private static List<File> getFiles(List<File> files, Path root) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    getFiles(files, path);
                } else {
                    files.add(path.toAbsolutePath().toFile());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
    
}
