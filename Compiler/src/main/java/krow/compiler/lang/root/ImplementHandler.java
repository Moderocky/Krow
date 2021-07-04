package krow.compiler.handler.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import krow.compiler.util.BracketReader;
import mx.kenzie.foundation.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class ImplementHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^implements?\\s*?<([^<>]*?)>");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("implement") && (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String group = matcher.group(1);
        final Type[] available = context.availableTypes().toArray(new Type[0]);
        final List<Type> list = new ArrayList<>();
        if (!group.isEmpty()) {
            final String[] imports = new BracketReader(group).splitIgnoreAll(',');
            for (final String string : imports) {
                final Signature signature = new Signature(string.trim(), available);
                list.add(signature.getOwner());
            }
        }
        data.interfaces.addAll(list);
        return new HandleResult(null, statement.substring(statement.indexOf('>') + 1).trim(), CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "IMPLEMENT";
    }
}
