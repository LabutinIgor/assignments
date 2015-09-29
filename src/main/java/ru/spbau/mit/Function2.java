package ru.spbau.mit;

abstract class Function2<T1, T2, R> {
    public abstract R apply(T1 arg1, T2 arg2);

    public <R2> Function2<T1, T2, R2> compose(final Function1<? super R, R2> outer) {
        final Function2<T1, T2, R> inner = this;

        return new Function2<T1, T2, R2>() {
            @Override
            public R2 apply(T1 newArg1, T2 newArg2) {
                return outer.apply(inner.apply(newArg1, newArg2));
            }
        };
    }

    public Function1<T2, R> bind1(final T1 newArg1) {
        final Function2<T1, T2, R> inner = this;

        return new Function1<T2, R>() {
            @Override
            public R apply(T2 newArg2) {
                return inner.apply(newArg1, newArg2);
            }
        };
    }

    public Function1<T1, R> bind2(final T2 newArg1) {
        final Function2<T1, T2, R> inner = this;

        return new Function1<T1, R>() {
            @Override
            public R apply(T1 newArg2) {
                return inner.apply(newArg2, newArg1);
            }
        };
    }

    public Function1<T1, Function1<T2, R>> curry() {
        return new Function1<T1, Function1<T2, R>>() {
            @Override
            public Function1<T2, R> apply(final T1 newArg1) {
                return bind1(newArg1);
            }
        };
    }
}
