package krow.compiler.lang.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreBracket;
import krow.compiler.pre.PreClass;
import mx.kenzie.foundation.CodeWriter;
import mx.kenzie.foundation.Type;
import mx.kenzie.foundation.WriteInstruction;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class IfHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^if\\s+\\(");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        if (!statement.startsWith("if")) return false;
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String input = matcher.group();
        final Label end = new Label();
        context.lookingFor = new Type(boolean.class);
        context.child.statement((writer, method) -> method.visitJumpInsn(153, end));
        context.child.swap(true);
        final PassJumpInstruction instruction = new PassJumpInstruction();
        instruction.end = end;
        context.child.elseJumps.add(instruction);
        context.child.skip(instruction);
        context.child.setBlockAllowed(true);
        final PreBracket bracket;
        context.brackets().add(0, bracket = new PreBracket());
        bracket.state = CompileState.METHOD_BODY;
        context.setConditionPhase(2);
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "IF";
    }
    
    public static class PassJumpInstruction implements WriteInstruction {
        
        public WriteInstruction jump;
        public Label end;
        
        @Override
        public void accept(CodeWriter codeWriter, MethodVisitor methodVisitor) {
            if (jump != null) jump.accept(codeWriter, methodVisitor);
            methodVisitor.visitLabel(end);
        }
    }
    
}
