package ru.noties.tasker;

/**
 * Created by Dimitry Ivanov on 03.08.2015.
 */
public enum DuplicateExecutionStrategy {
    REPLACE, // cancels current running
    DO_NOT_EXECUTE, // does not submit task
}
