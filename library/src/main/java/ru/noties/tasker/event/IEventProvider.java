package ru.noties.tasker.event;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public interface IEventProvider {

    void subscribe(Object who);
    void unsubscribe(Object who);

    boolean isRegistered(Object who);

    void post(Object what);

}
