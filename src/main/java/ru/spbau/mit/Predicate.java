package ru.spbau.mit;

public abstract class Predicate<T> extends Function1<T, Boolean> {
    public static final Predicate<Object> ALWAYS_TRUE =
            new Predicate<Object>() {
                @Override
                public Boolean apply(Object arg) {
                    return true;
                }
            };

    public static final Predicate<Object> ALWAYS_FALSE =
            new Predicate<Object>() {
                @Override
                public Boolean apply(Object arg) {
                    return false;
                }
            };

    public<T1 extends T> Predicate<T1> or(final Predicate<? super T1> outer) {
        final Predicate<T> inner = this;

        return new Predicate<T1>() {
            @Override
            public Boolean apply(T1 arg) {
                return inner.apply(arg) || outer.apply(arg);
            }
        };
    }

    public<T1 extends T> Predicate<T1> and(final Predicate<? super T1> outer) {
        final Predicate<T> inner = this;

        return new Predicate<T1>() {
            @Override
            public Boolean apply(T1 arg) {
                return inner.apply(arg) && outer.apply(arg);
            }
        };
    }

    public Predicate<T> not() {
        final Predicate<T> inner = this;

        return new Predicate<T>() {
            @Override
            public Boolean apply(T arg) {
                return !inner.apply(arg);
            }
        };
    }

}
