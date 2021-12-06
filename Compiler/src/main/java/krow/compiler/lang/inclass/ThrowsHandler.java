package krow.compiler.lang.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.Signature;
import krow.compiler.util.BracketReader;
import krow.compiler.util.HiddenModifier;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class ThrowsHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^throws\\s*?<(?<target>[^<>]*?)>");
    Matcher matcher;
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("throws") && (matcher = PATTERN.matcher(statement)).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final String group = matcher.group("target");
        if (!group.isEmpty()) {
            final Type[] types = context.availableTypes().toArray(new Type[0]);
            final String[] imports = new BracketReader(group).splitIgnoreAll(',');
            for (final String string : imports) {
                if (string.isEmpty()) continue;
                context.throwables.add(new Signature(string.trim(), types).getOwner());
            }
        }
        return new HandleResult(null, statement.substring(matcher.group().length()).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_THROWS";
    }
}
