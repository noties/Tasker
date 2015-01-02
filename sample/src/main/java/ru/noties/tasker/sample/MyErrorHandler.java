package ru.noties.tasker.sample;

import ru.noties.debug.Debug;
import ru.noties.tasker.task.Task;
import ru.noties.tasker.task.TaskManager;

public class MyErrorHandler implements TaskManager.ErrorHandler {

    @Override
    public void onExceptionHandled(Task task, Throwable throwable) {
        if (task != null) {

            if (task.notifyOnError()) {
                // notify on error
            }

            if (task.canRestart()) {
                // task can be restarted
            }

        }

        Debug.e(throwable, "in task: %s", task);
    }
}
