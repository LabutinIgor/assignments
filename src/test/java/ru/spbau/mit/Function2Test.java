package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

public class Function2Test {
    private static Function1<Integer, Integer> addTen =
            new Function1<Integer, Integer>() {
                @Override
                public Integer apply(Integer num) {
                    return num + 10;
                }
            };

    private static Function2<Integer, Integer, Integer> sum =
            new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer x, Integer y) {
                    return x + y;
                }
            };

    @Test
    public void testFunction1Apply() {
        assertTrue(10 == sum.apply(5, 5));
        assertTrue(20 == sum.apply(10, 10));
    }

    @Test
    public void testFunction2Compose() {
        Function2<Integer, Integer, Integer> addTenToSum = sum.compose(addTen);
        assertTrue(10 == addTenToSum.apply(0, 0));
        assertTrue(30 == addTenToSum.apply(5, 15));
    }

    @Test
    public void testFunction2Bind1() {
        Function1<Integer, Integer> addOne = sum.bind1(1);
        assertTrue(1 == addOne.apply(0));
        assertTrue(30 == addOne.apply(29));
    }

    @Test
    public void testFunction2Bind2() {
        Function1<Integer, Integer> addOne = sum.bind2(1);
        assertTrue(1 == addOne.apply(0));
        assertTrue(30 == addOne.apply(29));
    }
    @Test
    public void testFunction2Curry() {
        Function1<Integer, Function1<Integer, Integer>> sumCurry = sum.curry();
        assertTrue(4 == sumCurry.apply(2).apply(2));
        assertTrue(15 == sumCurry.apply(10).apply(5));
    }
}