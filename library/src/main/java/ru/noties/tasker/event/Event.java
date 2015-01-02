package ru.noties.tasker.event;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public class Event<T> {
    
    private Throwable throwable = null;
    private T value = null;

    public T getValue() {
        return value;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    // cast to sibling
    public <S extends Event<T>> S setThrowable(Throwable throwable) {
        this.throwable = throwable;
        // noinspection unchecked
        return (S) this;
    }

    // cast to sibling, so we can link init & setter calls
    // new SomeSiblingEvent().setValue(null);
    public <S extends Event<T>> S setValue(T value) {
        this.value = value;
        // noinspection unchecked
        return (S) this;
    }

    public boolean isError() {
        return throwable != null;
    }
}
