package krow.compiler.handler.inclass;

import krow.compiler.CompileContext;
import krow.compiler.CompileState;
import krow.compiler.HandleResult;
import krow.compiler.handler.Handler;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.Signature;
import krow.compiler.util.BracketReader;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportHandler implements Handler {
    private static final Pattern PATTERN = Pattern.compile("^import\\s*?<(.*?)>");
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("import") && PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String group = matcher.group(1);
        if (!group.isEmpty()) {
            final String[] imports = new BracketReader(group).splitIgnoreAll(',');
            for (final String string : imports) {
                new Signature(string.trim(), context.availableTypes().toArray(new Type[0])).pass(context.child);
            }
        }
        return new HandleResult(null, statement.substring(statement.indexOf('>') + 1).trim(), CompileState.IN_CLASS);
    }
    
    @Override
    public String debugName() {
        return "IMPORT";
    }
}
