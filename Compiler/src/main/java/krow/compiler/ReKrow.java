package krow.compiler;

import krow.compiler.api.*;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.PostCompileClass;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

@SuppressWarnings("UnusedLabel")
public class ReKrow implements Compiler<Krow>, HandlerInterface, KrowCompiler {
    
    protected final List<Library> libraries = new ArrayList<>();
    final Pattern LINE_COMMENT = Pattern.compile("//.+(?=(\\R|$))");
    final Pattern BLOCK_COMMENT = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    
    public ReKrow() {
        registerLibrary(SystemLibrary.SYSTEM_LIBRARY);
        registerLibrary(BinderLibrary.BINDER_LIBRARY);
        registerLibrary(MemoryLibrary.MEMORY_LIBRARY);
    }
    
    public List<Library> getLibraries() {
        return libraries;
    }
    
    @Override
    public void registerLibrary(Library library) {
        libraries.add(library);
    }
    
    public byte[] compile(final String file) {
        return this.compile0(file).builder.compile();
    }
    
    public CompileContext compile0(final String file) {
        final PreClass pre = new PreClass();
        final CompileContext context = new CompileContext();
        context.compiler = this;
        context.handlers = new HandlerSet();
        context.addCompileTimeLibrary("krow.lang");
        final String input = file
            .replaceAll(LINE_COMMENT.pattern(), "")
            .replaceAll(BLOCK_COMMENT.pattern(), "").trim();
        HandleResult result = new HandleResult(null, input.trim(), CompileState.FILE_ROOT);
        do {
            if (result.instruction() != null) result.instruction().run();
            result = this.compile(result.remainder(), result.future(), pre, context);
        } while (result != null);
        final ClassBuilder builder = context.builder;
        if (context.exported) builder.addModifiers(ACC_PUBLIC);
        return context;
    }
    
    protected HandleResult compile(final String statement, final CompileState state, final PreClass data, final CompileContext context) {
        final Handler handler = this.getHandler(statement, state, context);
        if ("1".equals(System.getProperty("TEST_STATE"))) {
            System.out.println(handler.owner().identifier() + "/" + handler.debugName());
        }
        return handler.handle0(statement, data, context, state);
    }
    
    public Handler getHandler(final String statement, final CompileState state, final CompileContext context) {
        return context.handlers.getHandler(statement, state, context);
    }
    
    @Override
    public PostCompileClass compileClass(final InputStream source) {
        final StringBuilder builder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
            (source, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final ClassBuilder finish = compile0(builder.toString()).builder;
        return new PostCompileClass(finish.compile(), finish.getName(), finish.getInternalName());
    }
    
    @Override
    public PostCompileClass[] compile(final InputStream source) {
        final StringBuilder builder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
            (source, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        final CompileContext context = compile0(builder.toString());
        final ClassBuilder finish = context.builder;
        final List<PostCompileClass> classes = new ArrayList<>(context.attachments);
        classes.add(new PostCompileClass(finish.compile(), finish.getName(), finish.getInternalName()));
        for (final ClassBuilder suppressed : finish.getSuppressed()) {
            classes.add(new PostCompileClass(suppressed.compile(), suppressed.getName(), suppressed.getInternalName()));
        }
        return classes.toArray(new PostCompileClass[0]);
    }
    
    @Override
    public void compileAndLoad(final InputStream source) {
        final StringBuilder builder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
            (source, StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        compileAndLoad(builder.toString());
    }
    
    public Class<?> compileAndLoad(final String file) {
        final CompileContext context = this.compile0(file);
        for (PostCompileClass attachment : context.attachments) {
            attachment.compileAndLoad();
        }
        final ClassBuilder builder = context.builder;
        return builder.compileAndLoad();
    }
    
    @Override
    public void compileResource(String main, File file, File... files) {
        final Set<PostCompileClass> classes = new HashSet<>();
        final List<File> extra = new ArrayList<>();
        final InputStream[] sources = new InputStream[files.length];
        try (
            final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i < sources.length; i++) {
                try {
                    if (files[i].getName().endsWith(".DS_Store")) continue;
                    if (files[i].getName().endsWith(".kro"))
                        sources[i] = new FileInputStream(files[i]);
                    else extra.add(files[i]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            for (final InputStream source : sources) {
                if (source == null) continue;
                classes.addAll(List.of(compile(source)));
            }
            for (final PostCompileClass result : classes) {
                ZipEntry entry = new ZipEntry(result.internalName() + ".class");
                out.putNextEntry(entry);
                byte[] data = result.code();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
            for (final File ext : extra) {
                ZipEntry entry = new ZipEntry(ext.getName());
                out.putNextEntry(entry);
                byte[] data = new FileInputStream(ext).readAllBytes();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
            manifest:
            {
                ZipEntry entry = new ZipEntry("META-INF/MANIFEST.MF");
                out.putNextEntry(entry);
                final String version = this.getClass().getPackage().getImplementationVersion();
                byte[] data = ("Manifest-Version: 1.0\n" +
                    (main != null ? "Main-Class: " + main + "\n" : "") +
                    "Archiver-Version: Zip\n" +
                    "Created-By: Krow Compiler " + version + "\n" +
                    "Built-By: Krow Compiler\n").getBytes(StandardCharsets.UTF_8);
                out.write(data, 0, data.length);
                out.closeEntry();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            for (final InputStream source : sources) {
                try {
                    source.close();
                } catch (Throwable ignored) {
                    throw new RuntimeException(ignored);
                }
            }
        }
    }
    
    @Override
    public void compileResource(String main, File file, InputStream... sources) {
        final List<PostCompileClass> classes = new ArrayList<>();
        for (final InputStream source : sources) {
            classes.addAll(List.of(compile(source)));
        }
        try (
            final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
            for (final PostCompileClass result : classes) {
                ZipEntry entry = new ZipEntry(result.internalName() + ".class");
                out.putNextEntry(entry);
                byte[] data = result.code();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
            manifest:
            {
                ZipEntry entry = new ZipEntry("META-INF/MANIFEST.MF");
                out.putNextEntry(entry);
                final String version = this.getClass().getPackage().getImplementationVersion();
                byte[] data = ("Manifest-Version: 1.0\n" +
                    (main != null ? "Main-Class: " + main + "\n" : "") +
                    "Archiver-Version: Zip\n" +
                    "Created-By: Krow Compiler " + version + "\n" +
                    "Built-By: Krow Compiler\n").getBytes(StandardCharsets.UTF_8);
                out.write(data, 0, data.length);
                out.closeEntry();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            for (final InputStream source : sources) {
                try {
                    source.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }
    
    @Override
    public Krow getLanguage() {
        return new Krow();
    }
}
