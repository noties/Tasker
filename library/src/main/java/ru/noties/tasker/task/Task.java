package ru.noties.tasker.task;

import ru.noties.tasker.event.Event;
import ru.noties.tasker.event.EventManager;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public abstract class Task implements ITask {

    private String mTag;
    private boolean mIsCancelled;

    void setTag(String tag) {
        this.mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public void setCancelled(boolean cancel) {
        mIsCancelled = cancel;
    }

    public boolean isCancelled() {
        return mIsCancelled;
    }

    private void finish() {

        if (mTag == null) {
            return;
        }

        TaskManager.get().setTaskComplete(mTag);
    }

    @Override
    public void run() {

        try {
            execute();
        } catch (Throwable e) {

            notifyErrorHandler(e);

            onExceptionHandled(e);
        }

        finish();
    }

    protected void onCatchedThrowable(Throwable throwable) {
        notifyErrorHandler(throwable);
    }

    private void notifyErrorHandler(Throwable throwable) {
        final TaskManager.ErrorHandler errorHandler = TaskManager.getErrorHandler();
        if (errorHandler != null) {
            errorHandler.onExceptionHandled(this, throwable);
        }
    }

    public boolean canRestart() {
        return true;
    }

    public boolean notifyOnError() {
        return true;
    }

    protected abstract void execute();

    protected void onExceptionHandled(Throwable throwable){}

    protected <E extends Event<?>> void post(E event) {
        // the least we can do is to skip notification
        if (!mIsCancelled) {
            EventManager.post(event);
        }
    }
}
