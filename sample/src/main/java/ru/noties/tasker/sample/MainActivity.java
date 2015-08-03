package ru.noties.tasker.sample;

import android.app.Activity;
import android.os.Bundle;

import ru.noties.debug.Debug;
import ru.noties.handle.Handle;
import ru.noties.handle.IEventHandler;
import ru.noties.handle.annotations.EventHandler;
import ru.noties.tasker.DuplicateExecutionStrategy;
import ru.noties.tasker.Tasker;

@EventHandler(MainActivity.MyEvent.class)
public class MainActivity extends Activity {

    final IEventHandler mEventHandler = new MainActivityEventHandler() {
        @Override
        public void onEvent(MyEvent event) {
            Debug.i("received event with value: %s", event.value);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private static void execute(int count) {
        for (int i = count; --i > -1; ) {
            final boolean result = Tasker.execute(new MyTask());
            Debug.i("result: %s", result);
        }
    }

    private static void executeDuplicateReplace(int count) {
        for (int i = count; --i > -1; ) {
            final boolean result = Tasker.execute(new MyTask(), "replace", DuplicateExecutionStrategy.REPLACE);
            Debug.i("result: %s", result);
        }
    }

    private static void executeDuplicateDoNotExecute(int count) {
        for (int i = count; --i > -1; ) {
            final boolean result = Tasker.execute(new MyTask(), "do_nothing", DuplicateExecutionStrategy.DO_NOT_EXECUTE);
            Debug.i("result: %s", result);
        }
    }

    static class MyEvent {
        final String value;
        MyEvent(String value) {
            this.value = value;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Handle.register(mEventHandler);

        final int count = 5;

        execute(count);
        executeDuplicateReplace(count);
        executeDuplicateDoNotExecute(count);
    }

    @Override
    public void onStop() {
        super.onStop();

        Handle.unregister(mEventHandler);
    }

    private static class MyTask extends BaseTask {

        @Override
        protected void execute() {
            try {
                Thread.sleep(1500L);
            } catch (InterruptedException e) {
                Debug.e(e);
            }
            post(new MyEvent(toString()));
        }
    }
}
