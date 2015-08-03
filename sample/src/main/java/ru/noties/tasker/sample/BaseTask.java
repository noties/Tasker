package ru.noties.tasker.sample;

import ru.noties.handle.Handle;
import ru.noties.tasker.Task;

/**
 * Created by Dimitry Ivanov on 03.08.2015.
 */
public abstract class BaseTask extends Task {

    protected void post(Object event) {
        Handle.post(event);
    }
}
