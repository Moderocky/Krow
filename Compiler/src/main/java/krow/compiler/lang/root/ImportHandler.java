package krow.compiler.lang.root;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import krow.compiler.util.BracketReader;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class ImportHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^import\\s*?<([^<>]*?)>");
    
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("import") && (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context, CompileState state) {
        final String group = matcher.group(1);
        if (!group.isEmpty()) {
            final String[] imports = new BracketReader(group).splitIgnoreAll(',');
            for (final String string : imports) {
                if (string.isEmpty()) continue;
                new Signature(string.trim(), context.availableTypes()
                    .toArray(new Type[0])).pass(context.child != null ? context.child : context);
            }
        }
        return new HandleResult(null, statement.substring(statement.indexOf('>') + 1).trim(), state);
    }
    
    @Override
    public String debugName() {
        return "IMPORT";
    }
}
