package ru.noties.tasker;

import ru.noties.tasker.event.EventManager;
import ru.noties.tasker.event.IEventProvider;
import ru.noties.tasker.task.TaskManager;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public class Tasker {

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

    private Tasker() {

    }

    private TaskManager mTaskManager;
    private EventManager mEventManager;

    public void init(Configuration configuration) {

        mTaskManager  = new TaskManager (configuration.errorHandler);
        mEventManager = new EventManager(configuration.eventProvider);

    }

    public TaskManager getTaskManager() {
        return mTaskManager;
    }

    public EventManager getEventManager() {
        return mEventManager;
    }

    public static class Configuration {

        private IEventProvider eventProvider;
        private TaskManager.ErrorHandler errorHandler;

        public Configuration setEventProvider(IEventProvider eventProvider) {
            this.eventProvider = eventProvider;
            return this;
        }

        public Configuration setErrorHandler(TaskManager.ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }
    }
}
