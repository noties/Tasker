package ru.noties.tasker;

import java.lang.reflect.Modifier;
import java.util.concurrent.Future;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public abstract class Task implements Runnable {

    volatile Future<?> mFuture;
    volatile String mTag;
    private volatile boolean isStarted;

    public Task() throws IllegalStateException {
        assertClass(getClass());
    }

    private static void assertClass(Class<?> cl) {
        if (cl.isAnonymousClass()) {
            throw new IllegalStateException(cl.toString() + " should not be anonymous class to prevent context leaks");
        }
        if (cl.isMemberClass()) {
            if ((cl.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
                throw new IllegalStateException(cl.toString() + " should be static");
            }
        }
    }

    private boolean mIsDispatchMainThread;

    public boolean isDispatchMainThread() {
        return mIsDispatchMainThread;
    }

    public void setIsDispatchMainThread(boolean isDispatchMainThread) {
        mIsDispatchMainThread = isDispatchMainThread;
    }

    void setTag(String tag) {
        this.mTag = tag;
    }

    void setFuture(Future<?> future) {
        mFuture = future;
    }

    public String getTag() {
        return mTag;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        final boolean result = mFuture != null && mFuture.cancel(mayInterruptIfRunning);
        if (result) {
            Tasker.getInstance().setTaskFinished(this);
        }
        return result;
    }

    public boolean isCancelled() {
        return mFuture != null && mFuture.isCancelled();
    }

    public boolean isDone() {
        return mFuture != null && mFuture.isDone();
    }

    public boolean isStarted() {
        return isStarted;
    }

    @Override
    public void run() {

        isStarted = true;

        try {
            execute();
        } catch (Throwable e) {
            final Tasker tasker = Tasker.getInstance();
            tasker.onException(this, e);
            if (!mIsDispatchMainThread) {
                onException(e);
            } else {
                final Throwable t = e;
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        onException(t);
                    }
                };
                tasker.dispatchMainThread(r);
            }
        } finally {
            Tasker.getInstance().setTaskFinished(this);
            if (!mIsDispatchMainThread) {
                onFinally();
            } else {
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        onFinally();
                    }
                };
                Tasker.getInstance().dispatchMainThread(r);
            }
        }
    }

    protected abstract void execute();

    protected void onException(Throwable throwable) {}
    protected void onFinally() {}

    protected void dispatchOnMainThread(Runnable r) {
        Tasker.getInstance().dispatchMainThread(r);
    }
}
