package edu.buffalo.cse.cse605.project2;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProxyQueue<T> implements Queue<T> {

    private final List<Future<T>> futureList;

    ProxyQueue(List<Future<T>> futureList) {

        this.futureList = futureList;
    }

    @Override
    public T remove() {
        while (true) {
            for (Future<T> aFuture : futureList) {
                if (aFuture.isDone()) {
                    try {
                        return aFuture.get();
                    } catch (InterruptedException e) {
                        throw new IllegalStateException("Interrupted.");
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e.getCause());
                    }
                }
            }
            if (futureList.isEmpty()) {
                throw new NoSuchElementException("Queue is empty!");
            }
        }

    }

    @Override
    public int size() {
        return futureList.size();
    }

    @Override
    public boolean isEmpty() {
        return futureList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private List<Future<T>> iteratorList = new ArrayList<Future<T>>(futureList);

            @Override
            public boolean hasNext() {
                return iteratorList.isEmpty();
            }

            @Override
            public T next() {
                while (true) {
                    for (Future<T> future : iteratorList) {
                        if (future.isDone()) {
                            try {
                                return future.get();
                            } catch (InterruptedException e) {
                                throw new IllegalStateException("Interrupted");
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e.getCause());
                            }
                        }
                    }

                    if (iteratorList.isEmpty()) {
                        throw new NoSuchElementException("Iterator has no more values to iterate!");
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().isInterrupted();
                    }
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("This method is not implemented.");
            }
        };
    }

    @Override
    public void clear() {
        for (Future<T> future : futureList) {
            future.cancel(true);
        }
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public <T1 extends Object> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public boolean offer(T t) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public T poll() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public T element() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public T peek() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }
}
