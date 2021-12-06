package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.Handler;
import krow.compiler.api.Library;
import krow.compiler.lang.inclass.AbstractHandler;
import krow.compiler.lang.inclass.FinalHandler;
import krow.compiler.lang.inmethodheader.MethodEndParameterHandler;
import krow.compiler.lang.inmethodheader.MethodParameterHandler;
import krow.compiler.lang.inmethodheader.MethodSplitParameterHandler;
import krow.compiler.lang.instatement.*;
import krow.compiler.lang.instatement.maths.*;
import krow.compiler.lang.instructheader.NamedVarLoadHandler;
import krow.compiler.lang.instructheader.StructCallEndHandler;
import krow.compiler.lang.instructheader.StructSplitParameterHandler;
import krow.compiler.lang.literal.*;
import krow.compiler.lang.root.DeadEndHandler;
import krow.compiler.lang.root.*;
import mx.kenzie.foundation.language.PostCompileClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class SystemLibrary implements Library, InternalLibrary {
    
    public static final SystemLibrary SYSTEM_LIBRARY = new SystemLibrary();
    
    private static final HandlerSet DEFAULT_HANDLERS = new HandlerSet();
    
    static {
        DEFAULT_HANDLERS.put(CompileState.FILE_ROOT, new ArrayList<>(List.of(
            new LibraryHandler(),
            new AbstractHandler(),
            new FinalHandler(),
            new ImportHandler(),
            new ExportHandler(),
            new ImplementHandler(),
            new InterfaceHandler(),
            new ClassHandler(),
            new ExtendsHandler(),
            new DropLevelHandler(),
            new DeadEndHandler()
        )));
        DEFAULT_HANDLERS.put(CompileState.CLASS_BODY, List.of(
            new krow.compiler.lang.inclass.UpLevelHandler(),
            new krow.compiler.lang.inclass.DeadEndHandler(),
            new krow.compiler.lang.inclass.DropLevelHandler(),
            new krow.compiler.lang.inclass.StaticHandler(),
            new krow.compiler.lang.inclass.AbstractHandler(),
            new krow.compiler.lang.inclass.FinalHandler(),
            new krow.compiler.lang.inclass.BridgeHandler(),
            new krow.compiler.lang.inclass.ThrowsHandler(),
            new krow.compiler.lang.inclass.SynchronizedHandler(),
            new krow.compiler.lang.inmethod.ConstHandler(),
            new krow.compiler.lang.root.ImportHandler(),
            new krow.compiler.lang.inclass.ExportHandler(),
            new krow.compiler.lang.inclass.ClinitStartHandler(),
            new krow.compiler.lang.inclass.ConstructorStartHandler(),
            new krow.compiler.lang.inclass.FieldHandler(),
            new krow.compiler.lang.inclass.MethodStartHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.METHOD_HEADER_DECLARATION, List.of(
            new MethodEndParameterHandler(),
            new MethodSplitParameterHandler(),
            new MethodParameterHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.IMPLICIT_STRUCT_HEADER, List.of(
            new StructCallEndHandler(),
            new StructSplitParameterHandler(),
            new NamedVarLoadHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.CONST_DECLARATION, List.of(
            new krow.compiler.lang.inconst.DeadEndHandler(),
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
        DEFAULT_HANDLERS.put(CompileState.METHOD_BODY, List.of(
            new krow.compiler.lang.inmethod.UpLevelHandler(),
            new krow.compiler.lang.inmethod.DeadEndHandler(),
            new krow.compiler.lang.inmethod.AssertHandler(),
            new krow.compiler.lang.inmethod.ReturnHandler(),
            new krow.compiler.lang.inmethod.ConstHandler(),
            new krow.compiler.lang.inmethod.LabelHandler(),
            new krow.compiler.lang.inmethod.GotoHandler(),
            new krow.compiler.lang.inmethod.IfHandler(),
            new krow.compiler.lang.inmethod.ElseHandler(),
            new krow.compiler.lang.inmethod.ThrowHandler(),
            new krow.compiler.lang.inmethod.DropLevelHandler(),
            new krow.compiler.lang.inblock.UpLevelHandler(),
            new krow.compiler.lang.inmethod.ConstructorCallStartHandler(),
            new krow.compiler.lang.inmethod.AssignVarHandler(),
            new krow.compiler.lang.inmethod.DeclareAssignVarHandler(),
            new krow.compiler.lang.inmethod.DeclareVarHandler(),
            new krow.compiler.lang.instatement.NewInstanceHandler(),
            new krow.compiler.lang.inmethod.TypeHandler(),
            new krow.compiler.lang.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.lang.instatement.VarLoadHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.STATEMENT, List.of(
            new krow.compiler.lang.instatement.DeadEndHandler(),
            new OpenBracketHandler(),
            new CloseBracketHandler(),
            new AddHandler(),
            new SubtractHandler(),
            new MultiplyHandler(),
            new DivideHandler(),
            new krow.compiler.lang.instatement.maths.DefaultHandler(),
            new EqualsHandler(),
            new IsNotNullHandler(),
            new InvertHandler(),
            new IsNullHandler(),
            new NegateHandler(),
            new AndHandler(),
            new OrHandler(),
            new krow.compiler.lang.instatement.AssignArrayHandler(),
            new krow.compiler.lang.instatement.LoadArrayHandler(),
            new ArrayLengthHandler(),
            new CastHandler(),
            new UShiftHandler(),
            new ShiftHandler(),
            new GTLTEQHandler(),
            new GTLTHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new ClassSuffixHandler(),
            new StructImplicitHandler(),
            new krow.compiler.lang.instatement.AllocateInstanceHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.lang.instatement.NewArrayHandler(),
            new krow.compiler.lang.instatement.NewDimArrayHandler(),
            new krow.compiler.lang.instatement.NewInstanceHandler(),
            new FieldAssignHandler(),
            new FieldAccessHandler(), // goes in either
            new DynamicFieldAssignHandler(),
            new DynamicFieldAccessHandler(), // goes in either
            new krow.compiler.lang.inmethod.TypeHandler(),
            new krow.compiler.lang.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.lang.instatement.VarLoadHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.METHOD_CALL_HEADER, List.of(
            new krow.compiler.lang.incall.MethodCallEndHandler(),
            new krow.compiler.lang.incall.MethodSplitParameterHandler(),
            new OpenBracketHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new IsNotNullHandler(),
            new InvertHandler(),
            new IsNullHandler(),
            new NegateHandler(),
            new ClassSuffixHandler(),
            new krow.compiler.lang.instatement.LoadArrayHandler(),
            new ArrayLengthHandler(),
            new StructImplicitHandler(),
            new CastHandler(),
            new NewArrayHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.lang.instatement.NewInstanceHandler(),
            new FieldAccessHandler(),
            new DynamicFieldAccessHandler(), // goes in either
            new krow.compiler.lang.inmethod.TypeHandler(),
            new krow.compiler.lang.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.lang.incall.VarLoadHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.IMPLICIT_ARRAY_HEADER, List.of(
            new krow.compiler.lang.inarrayheader.ArrayEndHandler(),
            new krow.compiler.lang.inarrayheader.ArraySplitParameterHandler(),
            new OpenBracketHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new IsNotNullHandler(),
            new InvertHandler(),
            new IsNullHandler(),
            new NegateHandler(),
            new ClassSuffixHandler(),
            new krow.compiler.lang.instatement.LoadArrayHandler(),
            new ArrayLengthHandler(),
            new StructImplicitHandler(),
            new CastHandler(),
            new NewArrayHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.lang.instatement.NewInstanceHandler(),
            new FieldAccessHandler(),
            new DynamicFieldAccessHandler(), // goes in either
            new krow.compiler.lang.inmethod.TypeHandler(),
            new krow.compiler.lang.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.lang.incall.VarLoadHandler()
        ));
    }
    
    private final List<PostCompileClass> list;
    
    {
        if ("2".equals(System.getProperty("TEST_STATE"))) list = new ArrayList<>();
        else list = List.of(
            InternalLibrary.getRuntimeDependency("krow.lang.Runtime", "Runtime.class"),
            InternalLibrary.getRuntimeDependency("krow.lang.Structure", "Structure.class")
        );
    }
    
    @Override
    public String identifier() {
        return "krow.lang";
    }
    
    @Override
    public String name() {
        return "Krow";
    }
    
    @Override
    public Collection<Handler> getHandlers(CompileState state) {
        return new ArrayList<>(DEFAULT_HANDLERS.get(state));
    }
    
    @Override
    public Collection<PostCompileClass> getRuntime() {
        if ("2".equals(System.getProperty("TEST_STATE"))) return new ArrayList<>();
        return list;
    }
    
}
