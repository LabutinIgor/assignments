package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

public abstract class Collections {
    public static<T, R> Iterable<R> map(final Function1<? super T, ? extends R> func, final Iterable<T> collection) {
        List<R> resCollection = new ArrayList<>();
        for (T el : collection) {
            resCollection.add(func.apply(el));
        }
        return resCollection;
    }

    public static<T> Iterable<T> filter(final Predicate<? super T> predicate, final Iterable<T> collection) {
        List<T> resCollection = new ArrayList<>();
        for (T el : collection) {
            if (predicate.apply(el))
                resCollection.add(el);
        }
        return resCollection;
    }

    public static<T> Iterable<T> takeWhile(final Predicate<? super T> predicate, final Iterable<T> collection) {
        List<T> resCollection = new ArrayList<>();
        for (T el : collection) {
            if (predicate.apply(el))
                resCollection.add(el);
            else
                break;
        }
        return resCollection;
    }

    public static<T> Iterable<T> takeUnless(final Predicate<? super T> predicate, final Iterable<T> collection) {
        return takeWhile(predicate.not(), collection);
    }

    public static<T, R> R foldl(final Function2<? super R, ? super T, ? extends R> func, R init, final Iterable<T> collection) {
        R res = init;
        for (T el : collection) {
            res = func.apply(res, el);
        }
        return res;
    }

    public static<T, R> R foldr(final Function2<? super T, ? super R, ? extends R> func, R init, final Iterable<T> collection) {
        List<T> list = new ArrayList<>();
        for (T el : collection) {
            list.add(el);
        }
        R res = init;
        java.util.Collections.reverse(list);
        for (T el : list) {
            res = func.apply(el, res);
        }
        return res;
    }

}
