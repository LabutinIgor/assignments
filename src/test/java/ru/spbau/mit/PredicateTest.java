package ru.spbau.mit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {
    private static Predicate<Integer> isZero =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer num) {
                    return num == 0;
                }
            };

    private static Predicate<Integer> divide =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer num) {
                    return 100 / num > 0;
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
        assertTrue(Predicate.ALWAYS_TRUE.or(divide).apply(0));
        assertFalse(Predicate.ALWAYS_FALSE.and(divide).apply(0));
    }
}
