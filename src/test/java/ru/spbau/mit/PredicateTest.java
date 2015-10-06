package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PredicateTest {
    private static Predicate<Integer> isZero =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer num) {
                    return num == 0;
                }
            };

    private static Predicate<Integer> failIfZero =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer num) {
                    if (num == 0) {
                        fail();
                    }
                    return true;
                }
            };
    @Test
    public void testPredicateOperations() {
        assertTrue(isZero.apply(0));
        assertFalse(isZero.and(Predicate.ALWAYS_FALSE).apply(0));
        assertTrue(Predicate.ALWAYS_TRUE.or(isZero).apply(1));
    }

    @Test
    public void testLazyOperations() {
        assertTrue(Predicate.ALWAYS_TRUE.or(failIfZero).apply(0));
        assertFalse(Predicate.ALWAYS_FALSE.and(failIfZero).apply(0));
    }
}
