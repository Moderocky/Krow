package krow.test;

import mx.kenzie.foundation.ClassBuilder;
import mx.kenzie.foundation.Type;
import org.junit.Test;

import static java.lang.reflect.Modifier.FINAL;
import static java.lang.reflect.Modifier.PUBLIC;
import static mx.kenzie.foundation.WriteInstruction.*;
import static mx.kenzie.foundation.WriteInstruction.returnEmpty;

public class AssumptionTest {
    
    @Test
    public void test() {
        for (int i : new int[]{'0', '1', '2', '3', '8', '9'}) {
            assert !(i < 48 || i > 57);
        }
    }
    
    private static boolean privateMethod() {
        return true;
    }
    
}
