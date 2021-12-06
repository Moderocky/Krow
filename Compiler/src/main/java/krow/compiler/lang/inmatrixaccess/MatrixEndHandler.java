package krow.compiler.lang.inmatrixaccess;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileExpectation;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreArray;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class MatrixEndHandler implements DefaultHandler {
    
    private static final Pattern PATTERN = Pattern.compile("^]\\s*=\\s*(?!=)");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        return statement.startsWith("]");
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final PreArray array = (PreArray) context.child.preparing.remove(0);
        final int substring;
        assert array != null;
        if (PATTERN.matcher(statement).find()) {
            substring = statement.indexOf('=') + 1;
            context.lookingFor = array.type.componentType().componentType();
            context.child.skip(PreVariable.store(array.type.componentType(), 0));
            context.child.point = null;
            context.child.staticState = false;
            context.expectation = CompileExpectation.OBJECT;
        } else {
            substring = 1;
            context.child.statement(PreVariable.load(array.type.componentType(), 0));
            context.child.point = array.type.componentType().componentType();
            context.child.staticState = false;
            context.expectation = CompileExpectation.NONE;
        }
        final CompileState state = context.child.nested.remove(0);
        return new HandleResult(null, statement.substring(substring)
            .trim(), state);
    }
    
    @Override
    public String debugName() {
        return "END_LOAD_MATRIX_ITEM";
    }
}
