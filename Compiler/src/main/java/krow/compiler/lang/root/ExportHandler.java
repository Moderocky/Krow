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
public class ExportHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^export\\s*?<([^<>]*?)>");
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("export") && PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        context.exported = true;
        final String group = matcher.group(1);
        if (!group.isEmpty()) {
            final String[] imports = new BracketReader(group).splitIgnoreAll(',');
            for (final String string : imports) {
                new Signature(string.trim(), context.availableTypes().toArray(new Type[0])).export(context);
            }
        }
        return new HandleResult(null, statement.substring(statement.indexOf('>') + 1).trim(), CompileState.FILE_ROOT);
    }
    
    @Override
    public String debugName() {
        return "EXPORT";
    }
}
