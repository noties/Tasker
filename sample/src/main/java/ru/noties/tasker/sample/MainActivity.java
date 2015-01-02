package ru.noties.tasker.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import ru.noties.debug.Debug;
import ru.noties.tasker.event.Event;
import ru.noties.tasker.event.EventManager;
import ru.noties.tasker.task.Task;
import ru.noties.tasker.task.TaskManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Debug.init(true);

        EventManager.subscribe(this);

        TaskManager.execute(new TestTask(), "test_task");
        TaskManager.execute(new TestTask2(), "test_task2");
    }

    @Override
    public void onDestroy() {
        EventManager.unsubscribe(this);
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(TestEvent2 event2) {
        Toast.makeText(this, String.valueOf(event2.getValue()), Toast.LENGTH_LONG).show();
    }

    private static class TestTask extends Task {

        @Override
        protected void execute() {

            try {
                Thread.sleep(2500L);
            } catch (InterruptedException e) {
                onCatchedThrowable(e);
            }

            throw new AssertionError("Error");
        }
    }

    private static class TestTask2 extends Task {

        @Override
        protected void execute() {

            try {
                Thread.sleep(1500L);
            } catch (InterruptedException e) {
                onCatchedThrowable(e);
            }

            post(new TestEvent2().setValue(SystemClock.elapsedRealtime()));
        }
    }

    private static class TestEvent2 extends Event<Long> {}
}
