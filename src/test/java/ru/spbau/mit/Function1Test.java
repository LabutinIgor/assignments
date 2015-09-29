package ru.spbau.mit;

import org.junit.Test;
import static org.junit.Assert.*;

public class Function1Test {
    private static Function1<Integer, Integer> addTen =
            new Function1<Integer, Integer>() {
                @Override
                public Integer apply(Integer num) {
                    return num + 10;
                }
            };

    private static class BaseClass {
    }

    private static class DerivedClass2 extends BaseClass {
    }

    private static class DerivedClass1 extends BaseClass {
    }

    private static Function1<BaseClass, BaseClass> idBase =
            new Function1<BaseClass, BaseClass>() {
                @Override
                public BaseClass apply(BaseClass arg) {
                    return arg;
                }
            };

    private static Function1<DerivedClass1, DerivedClass1> idDerivedClass1 =
            new Function1<DerivedClass1, DerivedClass1>() {
                @Override
                public DerivedClass1 apply(DerivedClass1 arg) {
                    return arg;
                }
            };

    @Test
    public void testFunction1Apply() {
        assertTrue(10 == addTen.apply(0));
        assertTrue(20 == addTen.apply(10));

        DerivedClass2 derivedClass2 = new DerivedClass2();
        DerivedClass1 derivedClass1 = new DerivedClass1();
        assertEquals(derivedClass2, idBase.apply(derivedClass2));
        assertNotEquals(idBase.apply(derivedClass2), idBase.apply(derivedClass1));
    }

    @Test
    public void testFunction1Compose() {
        Function1<Integer, Integer> addTwenty = addTen.compose(addTen);
        assertTrue(20 == addTwenty.apply(0));
        assertTrue(30 == addTwenty.apply(10));

        DerivedClass1 derivedClass1 = new DerivedClass1();
        Function1<DerivedClass1, BaseClass> idDog2 = idDerivedClass1.compose(idBase);
        assertEquals(derivedClass1, idDog2.apply(derivedClass1));
    }

}