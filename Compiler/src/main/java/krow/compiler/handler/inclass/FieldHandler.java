package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.Resolver;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreField;
import krow.compiler.pre.Signature;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^(?<type>" + Signature.TYPE_STRING + ")\\s+(?<name>" + Signature.IDENTIFIER + ")\\s*(?=;)");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String type = matcher.group("type");
        final String name = matcher.group("name");
        final PreField field = new PreField(data.path, Resolver.resolveType(type, context.availableTypes()
            .toArray(new Type[0])), name);
        field.modifiers |= context.upcoming();
//        if (!Modifier.isStatic(field.modifiers) && context.isInterface) throw new RuntimeException("Cannot place dynamic field in interface: '" + name + "'");
        context.builder.addField(field.name).setType(field.type).addModifiers(field.modifiers);
        context.availableFields.add(field);
        context.clearUpcoming();
        return new HandleResult(null, statement.substring(matcher.group().length()).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "DECLARE_FIELD";
    }
}
