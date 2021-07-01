package krow.test;

import org.junit.Test;

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
