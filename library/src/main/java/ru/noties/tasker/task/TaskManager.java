package ru.noties.tasker.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.noties.tasker.Tasker;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public final class TaskManager {

    public static void execute(Task task) {
        execute(task, null);
    }

    public static void execute(Task task, String tag) {
        execute(task, tag, false);
    }

    public static void execute(Task task, String tag, boolean cancelIfAnyWithTheSameTag) {
        get().executeTask(task, tag, cancelIfAnyWithTheSameTag);
    }

    public static TaskManager get() {
        return Tasker.getInstance().getTaskManager();
    }

    static ErrorHandler getErrorHandler() {
        return get().getHandler();
    }

    private final ExecutorService mExecutorService;
    private final Map<String, Task> mTags;
    private ErrorHandler mErrorHandler;

    public TaskManager() {
        this(null);
    }

    public TaskManager(ErrorHandler customErrorHandler) {
        mExecutorService    = Executors.newCachedThreadPool();
        mTags               = new HashMap<>();
        mErrorHandler       = customErrorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.mErrorHandler = errorHandler;
    }

    public void executeTask(Task task) {
        executeTask(task, null);
    }

    // falls silently if task with this tag is already added and not finished yet
    public void executeTask(Task task, String tag) {
        executeTask(task, tag, false);
    }

    public void executeTask(Task task, String tag, boolean cancelIfAnyWithTheSameTag) {

        // if tag not null fall in
        if (tag != null) {

            final boolean result = mTags.containsKey(tag);

            // if we have such tag
            if (result) {

                // and want to cancel it from posting result
                if (cancelIfAnyWithTheSameTag) {

                    final Task running = mTags.get(tag);

                    // do it manually
                    if (running != null) {
                        running.setCancelled(true);
                    }

                } else {
                    return;
                }

            }

            task.setTag(tag);
            mTags.put(tag, task);
        }

        mExecutorService.execute(task);
    }

    public void cancel(String tag) {
        final Task running = mTags.get(tag);
        if (running != null) {
            running.setCancelled(true);
        }
    }

    public boolean hasTaskWithTag(String tag) {
        return mTags.containsKey(tag);
    }

    void setTaskComplete(String tag) {
        mTags.remove(tag);
    }

    ErrorHandler getHandler() {
        return mErrorHandler;
    }

    public static interface ErrorHandler {
        void onExceptionHandled(Task task, Throwable throwable);
    }
}
