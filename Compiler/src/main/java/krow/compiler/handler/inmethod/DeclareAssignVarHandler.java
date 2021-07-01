package krow.compiler.handler.inmethod;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.Resolver;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreVariable;
import krow.compiler.pre.Signature;
import krow.lang.Structure;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeclareAssignVarHandler implements Handler {
    
    private static final Pattern PATTERN = Pattern.compile("^(?:final )?(?<type>" + Signature.TYPE_STRING + ")\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*=");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement, CompileContext context) {
        switch (context.expectation) {
            case TYPE, DEAD_END, LITERAL, VARIABLE, SMALL, OBJECT:
                return false;
        }
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String input = matcher.group();
        final String target = matcher.group("type");
        final String name = matcher.group("name");
        final Type type = Resolver.resolveType(target, context.availableTypes().toArray(new Type[0]));
        assert type != null;
        assert (context.getVariable(name) == null);
        final PreVariable assignment;
        context.lookingFor = type;
        context.duplicate = true;
        context.child.variables.add(assignment = new PreVariable(name, type));
        context.child.skip = assignment.store(context.getSlot(assignment));
        if (type.matches(Structure.class)) { // need to grab subclass from implicit declaration
            context.child.awaitAdjustedType = true;
            context.child.forAdjustment = assignment;
        }
        return new HandleResult(null, statement.substring(input.length()).trim(), CompileState.IN_STATEMENT);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_VARIABLE_AND_STORE";
    }
}
