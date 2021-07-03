package krow.compiler;

import krow.compiler.api.CompileState;
import krow.compiler.api.Library;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SystemLibrary implements Library {
    
    static final SystemLibrary SYSTEM_LIBRARY = new SystemLibrary();
    
    protected static final HandlerSet DEFAULT_HANDLERS = new HandlerSet();
    
    static {
        DEFAULT_HANDLERS.put(CompileState.FILE_ROOT, new ArrayList<>(List.of(
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
            new krow.compiler.handler.inclass.UpLevelHandler(),
            new krow.compiler.handler.inclass.DeadEndHandler(),
            new krow.compiler.handler.inclass.DropLevelHandler(),
            new krow.compiler.handler.inclass.StaticHandler(),
            new krow.compiler.handler.inclass.AbstractHandler(),
            new krow.compiler.handler.inclass.FinalHandler(),
            new krow.compiler.handler.inclass.BridgeHandler(),
            new krow.compiler.handler.inclass.SynchronizedHandler(),
            new krow.compiler.handler.inmethod.ConstHandler(),
            new krow.compiler.handler.root.ImportHandler(),
            new krow.compiler.handler.inclass.ExportHandler(),
            new krow.compiler.handler.inclass.ClinitStartHandler(),
            new krow.compiler.handler.inclass.ConstructorStartHandler(),
            new krow.compiler.handler.inclass.FieldHandler(),
            new krow.compiler.handler.inclass.MethodStartHandler()
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
        DEFAULT_HANDLERS.put(CompileState.METHOD_BODY, List.of(
            new krow.compiler.handler.inmethod.UpLevelHandler(),
            new krow.compiler.handler.inmethod.DeadEndHandler(),
            new krow.compiler.handler.inmethod.AssertHandler(),
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
        DEFAULT_HANDLERS.put(CompileState.STATEMENT, List.of(
            new krow.compiler.handler.instatement.DeadEndHandler(),
            new OpenBracketHandler(),
            new CloseBracketHandler(),
            new AddHandler(),
            new SubtractHandler(),
            new MultiplyHandler(),
            new DivideHandler(),
            new krow.compiler.handler.instatement.maths.DefaultHandler(),
            new EqualsHandler(),
            new InvertHandler(),
            new IsNullHandler(),
            new NegateHandler(),
            new AndHandler(),
            new OrHandler(),
            new krow.compiler.handler.instatement.AssignArrayHandler(),
            new krow.compiler.handler.instatement.LoadArrayHandler(),
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
            new StructImplicitHandler(),
            new krow.compiler.handler.instatement.AllocateInstanceHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.handler.instatement.NewArrayHandler(),
            new krow.compiler.handler.instatement.NewDimArrayHandler(),
            new krow.compiler.handler.instatement.NewInstanceHandler(),
            new FieldAssignHandler(),
            new FieldAccessHandler(), // goes in either
            new DynamicFieldAssignHandler(),
            new DynamicFieldAccessHandler(), // goes in either
            new krow.compiler.handler.inmethod.TypeHandler(),
            new krow.compiler.handler.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.handler.instatement.VarLoadHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.METHOD_CALL_HEADER, List.of(
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
            new InvertHandler(),
            new IsNullHandler(),
            new NegateHandler(),
            new krow.compiler.handler.instatement.LoadArrayHandler(),
            new ArrayLengthHandler(),
            new StructImplicitHandler(),
            new CastHandler(),
            new NewArrayHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.handler.instatement.NewInstanceHandler(),
            new FieldAccessHandler(),
            new DynamicFieldAccessHandler(), // goes in either
            new krow.compiler.handler.inmethod.TypeHandler(),
            new krow.compiler.handler.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.handler.incall.VarLoadHandler()
        ));
        DEFAULT_HANDLERS.put(CompileState.IMPLICIT_ARRAY_HEADER, List.of(
            new krow.compiler.handler.inarrayheader.ArrayEndHandler(),
            new krow.compiler.handler.inarrayheader.ArraySplitParameterHandler(),
            new OpenBracketHandler(),
            new NullLiteralHandler(),
            new BooleanLiteralHandler(),
            new CharLiteralHandler(),
            new StringLiteralHandler(),
            new SmallLiteralHandler(),
            new LongLiteralHandler(),
            new DoubleLiteralHandler(),
            new FloatLiteralHandler(),
            new InvertHandler(),
            new IsNullHandler(),
            new NegateHandler(),
            new krow.compiler.handler.instatement.LoadArrayHandler(),
            new ArrayLengthHandler(),
            new StructImplicitHandler(),
            new CastHandler(),
            new NewArrayHandler(),
            new DynamicCallStartHandler(), // goes in either
            new MethodCallStartHandler(), // goes in either
            new krow.compiler.handler.instatement.NewInstanceHandler(),
            new FieldAccessHandler(),
            new DynamicFieldAccessHandler(), // goes in either
            new krow.compiler.handler.inmethod.TypeHandler(),
            new krow.compiler.handler.inmethod.InitCallStartHandler(),
//            new krow.compiler.handler.instatement.HandleHandler(),
            new krow.compiler.handler.incall.VarLoadHandler()
        ));
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
    public Collection<DefaultHandler> getHandlers(CompileState state) {
        return new ArrayList<>(DEFAULT_HANDLERS.get(state));
    }
    
}
