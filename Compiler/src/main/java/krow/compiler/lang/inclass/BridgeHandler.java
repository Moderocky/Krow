package krow.compiler.lang.inclass;

import krow.compiler.CompileContext;
import krow.compiler.DefaultHandler;
import krow.compiler.api.CompileState;
import krow.compiler.api.HandleResult;
import krow.compiler.pre.PreClass;
import krow.compiler.pre.PreMethod;
import krow.compiler.pre.Signature;
import krow.compiler.util.HiddenModifier;
import mx.kenzie.foundation.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("ALL")
public class BridgeHandler implements DefaultHandler {
    private static final Pattern PATTERN = Pattern.compile("^bridge\\s*?<(?<target>.*?)>");
    
    @Override
    public boolean accepts(String statement) {
        return statement.startsWith("bridge") && PATTERN.matcher(statement).find();
    }
    
    @Override
    public HandleResult handle(String statement, PreClass data, CompileContext context) {
        final Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        final String group = matcher.group("target");
        final Signature signature = new Signature(group.trim(), context.availableTypes().toArray(new Type[0]));
        assert signature.getType() == Signature.Mode.METHOD;
        final PreMethod method = context.findMethod(signature);
        context.addUpcoming(HiddenModifier.BRIDGE);
        context.addUpcoming(HiddenModifier.SYNTHETIC);
        context.child.bridgeTarget = method;
        return new HandleResult(null, statement.substring(statement.indexOf('>') + 1).trim(), CompileState.CLASS_BODY);
    }
    
    @Override
    public String debugName() {
        return "UPCOMING_BRIDGE";
    }
}
