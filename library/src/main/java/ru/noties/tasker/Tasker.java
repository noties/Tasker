package ru.noties.tasker;

import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public class Tasker {

    public interface ErrorHandler {
        void onExceptionHandled(Task task, Throwable throwable);
    }

    private static volatile Tasker sInstance = null;

    public static Tasker getInstance() {
        Tasker localInstance = sInstance;
        if (localInstance == null) {
            synchronized (Tasker.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    localInstance = sInstance = new Tasker();
                }
            }
        }
        return localInstance;
    }

    // init with defaults
    public static void init() {
        Tasker.getInstance()._init(
                Executors.newCachedThreadPool(),
                null,
                false
        );
    }

    public static void init(ExecutorService executorService) {
        Tasker.getInstance()._init(
                executorService,
                null,
                false
        );
    }

    public static void init(ErrorHandler errorHandler, boolean dispatchErrorOnMainThread) {
        Tasker.getInstance()._init(
                Executors.newCachedThreadPool(),
                errorHandler,
                dispatchErrorOnMainThread
        );
    }

    public static void init(ExecutorService executorService, ErrorHandler errorHandler, boolean dispatchErrorOnMainThread) {
        Tasker.getInstance()._init(
                executorService,
                errorHandler,
                dispatchErrorOnMainThread
        );
    }

    public static boolean execute(Task task) {
        return Tasker.getInstance()._execute(task, null, null);
    }

    public static boolean execute(Task task, String tag) {
        return Tasker.getInstance()._execute(task, tag, DuplicateExecutionStrategy.DO_NOT_EXECUTE);
    }

    public static boolean execute(Task task, String tag, DuplicateExecutionStrategy strategy) {
        return Tasker.getInstance()._execute(task, tag, strategy);
    }

    public static boolean hasRunningTask(String tag) {
        return Tasker.getInstance().mTasks.containsKey(tag);
    }

    private final Handler mMainThread;
    private final Map<String, Task> mTasks;

    private ExecutorService mExecutorService;
    private ErrorDispatcher mErrorDispatcher;

    private Tasker() {
        mMainThread = new Handler(Looper.getMainLooper());
        mTasks = Collections.synchronizedMap(new HashMap<String, Task>());
    }

    private void _init(ExecutorService executorService, ErrorHandler errorHandler, boolean dispatchErrorOnMainThread) {
        mExecutorService = executorService;
        mErrorDispatcher = ErrorDispatcher.obtain(dispatchErrorOnMainThread, errorHandler, mMainThread);
    }

    private boolean _execute(Task task, String tag, DuplicateExecutionStrategy strategy) {

        if (tag != null && strategy != null) {

            final boolean hasRunningTaskWithSameTag = mTasks.containsKey(tag);
            if (hasRunningTaskWithSameTag) {
                // resolve
                switch (strategy) {

                    case REPLACE:
                        final Task r = mTasks.get(tag);
                        if (r != null) {
                            r.cancel(true);
                        }
                        break;

                    case DO_NOT_EXECUTE:
                        return false;

                    default:
                        throw new IllegalStateException("Unknown type: " + strategy);
                }
            }

            task.setTag(tag);
            mTasks.put(tag, task);
        }

        final Future<?> f = mExecutorService.submit(task);
        task.setFuture(f);
        return true;
    }

    void setTaskFinished(Task task) {
        final String tag = task.mTag;
        if (tag == null) {
            return;
        }
        mTasks.remove(tag);
    }

    void dispatchMainThread(Runnable r) {
        mMainThread.post(r);
    }

    void onException(Task task, Throwable t) {
        mErrorDispatcher.dispatch(task, t);
    }
}
