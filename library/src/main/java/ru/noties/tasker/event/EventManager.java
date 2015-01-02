package ru.noties.tasker.event;

import ru.noties.tasker.Tasker;

/**
 * Created by Dimitry Ivanov (noties.app@gmail.com) on 02.01.2015.
 */
public final class EventManager {

    public static EventManager get() {
        return Tasker.getInstance().getEventManager();
    }

    public static void subscribe(Object who) {
        get().subscribeInner(who);
    }

    public static void unsubscribe(Object who) {
        get().unsubscribeInner(who);
    }

    public static boolean isRegistered(Object who) {
        return get().isRegisteredInner(who);
    }

    public static <E extends Event<?>> void post(E event) {
        get().postInner(event);
    }

    private final IEventProvider mProvider;

    public EventManager(IEventProvider provider) {
        this.mProvider = provider;
    }

    public void subscribeInner(Object who) {
        if (!isRegisteredInner(who)) {
            mProvider.subscribe(who);
        }
    }

    public void unsubscribeInner(Object who) {
        if (isRegisteredInner(who)) {
            mProvider.unsubscribe(who);
        }
    }

    private boolean isRegisteredInner(Object who) {
        return mProvider.isRegistered(who);
    }

    private <E extends Event<?>> void postInner(E event) {
        mProvider.post(event);
    }
}
