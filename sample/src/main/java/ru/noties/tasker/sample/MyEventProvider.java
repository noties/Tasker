package ru.noties.tasker.sample;

import de.greenrobot.event.EventBus;
import ru.noties.tasker.event.IEventProvider;

public class MyEventProvider implements IEventProvider {

    @Override
    public void subscribe(Object who) {
        getBus().register(who);
    }

    @Override
    public void unsubscribe(Object who) {
        getBus().unregister(who);
    }

    @Override
    public boolean isRegistered(Object who) {
        return getBus().isRegistered(who);
    }

    @Override
    public void post(Object what) {
        getBus().post(what);
    }

    private EventBus getBus() {
        return EventBus.getDefault();
    }
}
