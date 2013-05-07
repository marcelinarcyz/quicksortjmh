package org.sortbench.concurrency;

import java.util.NoSuchElementException;

public abstract class PersistentImmutableList<T> {
    public abstract boolean empty();
    public abstract T head();
    public abstract PersistentImmutableList<T> tail();

    public PersistentImmutableList<T> prepend(T element) {
        return new NonEmptyList<T>(element, this);
    }

    public static <T> PersistentImmutableList<T> emptyList() {
        return new EmptyList<T>();
    }

    final static class EmptyList<T> extends PersistentImmutableList<T> {
        @Override public boolean empty() { return true; }
        @Override public T head() { throw new NoSuchElementException("can not call head of empty list"); }
        @Override public PersistentImmutableList<T> tail() { throw new UnsupportedOperationException("can not call tail of empty list"); }
    }

    final static class NonEmptyList<T> extends PersistentImmutableList<T> {
        @Override public boolean empty() { return false; }
        @Override public T head() { return _head; }
        @Override public PersistentImmutableList<T> tail() { return _tail; }

        private final T _head;
        private final PersistentImmutableList<T> _tail;

        NonEmptyList(T head, PersistentImmutableList<T> tail) {
            this._head = head;
            this._tail = tail;
        }
    }
}
