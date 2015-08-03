package ru.noties.tasker.sample;

import android.app.Application;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;
import ru.noties.tasker.Task;
import ru.noties.tasker.Tasker;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
        Tasker.init(new MyErrorHandler(), true);
    }

    private static class MyErrorHandler implements Tasker.ErrorHandler {

        @Override
        public void onExceptionHandled(Task task, Throwable throwable) {
            Debug.e(throwable, "Exception in task: %s, with tag: %s", task.getClass().getCanonicalName(), task.getTag());
        }
    }
}
