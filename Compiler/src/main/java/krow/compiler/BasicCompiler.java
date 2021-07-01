package krow.compiler;

import krow.compiler.handler.Handler;
import krow.compiler.handler.inclass.AbstractHandler;
import krow.compiler.handler.inclass.FinalHandler;
import krow.compiler.handler.inmethodheader.MethodEndParameterHandler;
import krow.compiler.handler.inmethodheader.MethodParameterHandler;
import krow.compiler.handler.inmethodheader.MethodSplitParameterHandler;
import krow.compiler.handler.instatement.*;
import krow.compiler.handler.instatement.maths.*;
import krow.compiler.handler.instructheader.NamedVarLoadHandler;
import krow.compiler.handler.instructheader.StructCallEndHandler;
import krow.compiler.handler.instructheader.StructSplitParameterHandler;
import krow.compiler.handler.literal.*;
import krow.compiler.handler.root.DeadEndHandler;
import krow.compiler.handler.root.*;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.language.Compiler;
import mx.kenzie.foundation.language.PostCompileClass;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

public class BasicCompiler implements Compiler<Krow> {
    
    Pattern LINE_COMMENT = Pattern.compile("//.+(?=(\\R|$))");
    Pattern BLOCK_COMMENT = Pattern.compile("/\\*[\\s\\S]*?\\*/");
    
    protected static final Map<CompileState, List<Handler>> HANDLERS = new HashMap<>();
    
    static {
        HANDLERS.put(CompileState.ROOT, List.of(
            new AbstractHandler(),
            new FinalHandler(),
            new ImportHandler(),
            new ExportHandler(),
            new ClassHandler(),
            new ExtendsHandler(),
            new DropLevelHandler(),
            new DeadEndHandler()
        ));
        HANDLERS.put(CompileState.IN_CLASS, List.of(
            new krow.compiler.handler.inclass.UpLevelHandler(),
            new krow.compiler.handler.inclass.DeadEndHandler(),
            new krow.compiler.handler.inclass.DropLevelHandler(),
            new krow.compiler.handler.inclass.StaticHandler(),
            new krow.compiler.handler.inclass.AbstractHandler(),
            new krow.compiler.handler.inclass.FinalHandler(),
            new krow.compiler.handler.inclass.BridgeHandler(),
            new krow.compiler.handler.inclass.SynchronizedHandler(),
            new krow.compiler.handler.inmethod.ConstHandler(),
            new krow.compiler.handler.inclass.ImportHandler(),
            new krow.compiler.handler.inclass.ExportHandler(),
            new krow.compiler.handler.inclass.ConstructorStartHandler(),
            new krow.compiler.handler.inclass.MethodStartHandler()
        ));
        HANDLERS.put(CompileState.IN_METHOD_HEADER, List.of(
            new MethodEndParameterHandler(),
            new MethodSplitParameterHandler(),
            new MethodParameterHandler()
        ));
        HANDLERS.put(CompileState.IN_STRUCT_HEADER, List.of(
            new StructCallEndHandler(),
            new StructSplitParameterHandler(),
            new NamedVarLoadHandler()
        ));
        HANDLERS.put(CompileState.IN_CONST, List.of(
            new krow.compiler.handler.inconst.DeadEndHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new BootstrapHandler()
        ));
        HANDLERS.put(CompileState.IN_METHOD, List.of(
            new krow.compiler.handler.inmethod.UpLevelHandler(),
            new krow.compiler.handler.inmethod.DeadEndHandler(),
            new krow.compiler.handler.inmethod.ReturnHandler(),
            new krow.compiler.handler.inmethod.ConstHandler(),
            new krow.compiler.handler.inmethod.LabelHandler(),
            new krow.compiler.handler.inmethod.GotoHandler(),
            new krow.compiler.handler.inmethod.IfHandler(),
            new krow.compiler.handler.inmethod.ConstructorCallStartHandler(),
            new krow.compiler.handler.inmethod.AssignVarHandler(),
            new krow.compiler.handler.inmethod.DeclareAssignVarHandler(),
            new krow.compiler.handler.inmethod.DeclareVarHandler(),
            new krow.compiler.handler.instatement.NewInstanceHandler(),
            new krow.compiler.handler.inmethod.TypeHandler(),
            new krow.compiler.handler.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.handler.instatement.VarLoadHandler()
        ));
        HANDLERS.put(CompileState.IN_STATEMENT, List.of(
            new krow.compiler.handler.instatement.DeadEndHandler(),
            new OpenBracketHandler(),
            new CloseBracketHandler(),
            new AddHandler(),
            new SubtractHandler(),
            new MultiplyHandler(),
            new DivideHandler(),
            new DefaultHandler(),
            new EqualsHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new StructImplicitHandler(),
            new krow.compiler.handler.instatement.AllocateInstanceHandler(),
            new CastHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.handler.instatement.NewInstanceHandler(),
            new FieldAssignHandler(),
            new FieldAccessHandler(), // goes in either
            new krow.compiler.handler.inmethod.TypeHandler(),
            new krow.compiler.handler.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.handler.instatement.VarLoadHandler()
        ));
        HANDLERS.put(CompileState.IN_CALL, List.of(
            new krow.compiler.handler.incall.MethodCallEndHandler(),
            new krow.compiler.handler.incall.MethodSplitParameterHandler(),
            new OpenBracketHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new StructImplicitHandler(),
            new CastHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.handler.instatement.NewInstanceHandler(),
            new FieldAccessHandler(),
            new krow.compiler.handler.inmethod.TypeHandler(),
            new krow.compiler.handler.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.handler.incall.VarLoadHandler()
        ));
    }
    
    protected Handler getHandler(final String statement, final CompileState state, final CompileContext context) {
        for (final Handler handler : HANDLERS.get(state)) {
            if (handler.accepts(statement, context)) return handler;
        }
        throw new RuntimeException("No handler matches: '" + statement + "' in " + state.name());
    }
    
    protected HandleResult compile(final String statement, final CompileState state, final PreClass data, final CompileContext context) {
        final Handler handler = this.getHandler(statement, state, context);
        if ("1".equals(System.getProperty("TEST_STATE"))) System.out.println(handler.debugName());
        return handler.handle0(statement, data, context, state);
    }
    
    public ClassBuilder compile0(final String file) {
        final PreClass pre = new PreClass();
        final CompileContext context = new CompileContext();
        final String input = file
            .replaceAll(LINE_COMMENT.pattern(), "")
            .replaceAll(BLOCK_COMMENT.pattern(), "").trim();
        HandleResult result = new HandleResult(null, input.trim(), CompileState.ROOT);
        do {
            result = this.compile(result.remainder(), result.future(), pre, context);
        } while (result != null);
        final ClassBuilder builder = context.builder;
        if (context.exported) builder.addModifiers(ACC_PUBLIC);
        return builder;
    }
    
    public byte[] compile(final String file) {
        return this.compile0(file).compile();
    }
    
    public Class<?> compileAndLoad(final String file) {
        final ClassBuilder builder = this.compile0(file);
        return builder.compileAndLoad();
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
        final ClassBuilder finish = compile0(builder.toString());
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
        final ClassBuilder finish = compile0(builder.toString());
        final List<PostCompileClass> classes = new ArrayList<>();
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
    
    @Override
    public void compileResource(String main, File file, File... files) {
        final List<PostCompileClass> classes = new ArrayList<>();
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
