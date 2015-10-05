package ru.spbau.mit;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CollectionsTest {
    private static Function1<Integer, Integer> addTen =
            new Function1<Integer, Integer>() {
                @Override
                public Integer apply(Integer arg) {
                    return arg + 10;
                }
            };

    private static Predicate<Integer> isOdd =
            new Predicate<Integer>() {
                @Override
                public Boolean apply(Integer num) {
                    return num % 2 == 1;
                }
            };

    private static Function2<String, Integer, String> addToString =
            new Function2<String, Integer, String>() {
                @Override
                public String apply(String arg1, Integer arg2) {
                    return arg1.concat(arg2.toString());
                }
            };

    private static Function2<Integer, Integer, Integer> substract =
            new Function2<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer arg1, Integer arg2) {
                    return arg1 - arg2;
                }
            };


    @Test
    public void testMap() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            list.add(i);
        Iterable<Integer> res = Collections.map(addTen, list);

        int i = 0;
        for (int el : res) {
            assertEquals(el, i + 10);
            i++;
        }
        assertEquals(i, 10);
    }

    @Test
    public void testFilter() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            list.add(i);
        Iterable<Integer> res = Collections.filter(isOdd, list);
        int i = 1;
        for (int el : res) {
            assertEquals(el, i);
            i += 2;
        }
        assertEquals(i, 11);
    }

    @Test
    public void testTakeWhileAndUnless() {
        List<Integer> list = new ArrayList<>();
        list.add(2);
        for (int i = 0; i < 10; i++)
            list.add(i);
        Iterable<Integer> res = Collections.takeWhile(isOdd.not(), list);
        Iterable<Integer> res2 = Collections.takeUnless(isOdd, list);
        assertEquals(res, res2);
        Iterator<Integer> it = res.iterator();
        assertTrue(it.next() == 2);
        assertTrue(it.next() == 0);
        assertFalse(it.hasNext());
    }

    @Test
    public void testFoldl() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            list.add(i);
        assertEquals(Collections.foldl(addToString, "", list), "0123456789");

        List<Integer> list2 = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            list2.add(i);

        assertEquals(Collections.foldl(substract, 0, list2), Integer.valueOf(-10));
    }

    @Test
    public void testFoldr() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            list.add(i);

        assertEquals(Collections.foldr(substract, 0, list), Integer.valueOf(2));
    }
}
