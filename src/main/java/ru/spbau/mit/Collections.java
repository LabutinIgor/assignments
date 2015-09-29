package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public abstract class Collections {
    public static<T, R> Iterable<R> map(final Function1<? super T, R> func, final Iterable<T> collection) {
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

    public static<T, R> R foldl(final Function2<R, ? super T, R> func, R init, final Iterable<T> collection) {
        R res = init;
        for (T el : collection) {
            res = func.apply(res, el);
        }
        return res;
    }

    private static<T> Iterable<T> reverse(final Iterable<T> collection) {
        List<T> list = new ArrayList<>();
        for (T el : collection) {
            list.add(el);
        }
        List<T> res = new ArrayList<>();
        ListIterator<T> it = list.listIterator(list.size());
        while (it.hasPrevious()) {
            T el = it.previous();
            res.add(el);
        }
        return res;
    }

    public static<T, R> R foldr(final Function2<? super T, R, R> func, R init, final Iterable<T> collection) {
        R res = init;
        Iterable<T> reverseCollection = reverse(collection);
        for (T el : reverseCollection) {
            res = func.apply(el, res);
        }
        return res;
    }

}
