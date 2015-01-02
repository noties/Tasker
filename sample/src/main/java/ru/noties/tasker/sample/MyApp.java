package ru.noties.tasker.sample;

import android.app.Application;

import ru.noties.tasker.Tasker;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Tasker.Configuration configuration = new Tasker.Configuration()
                .setErrorHandler(new MyErrorHandler())
                .setEventProvider(new MyEventProvider());

        Tasker.getInstance().init(configuration);
    }
}
