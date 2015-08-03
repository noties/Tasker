package ru.noties.tasker.test;

import android.os.Looper;

import junit.framework.TestCase;

import ru.noties.tasker.DuplicateExecutionStrategy;
import ru.noties.tasker.Task;
import ru.noties.tasker.Tasker;

/**
 * Created by Dimitry Ivanov on 03.08.2015.
 */
public class TaskTest extends TestCase {

    public void testAnonymous() {
        Tasker.init();
        try {
            new Task() {
                @Override
                protected void execute() {

                }
            };
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    public void testMemberStatic() {
        Tasker.init();
        try {
            new NonStaticClass();
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }

    public void testNormal() {
        Tasker.init();
        try {
            new SimpleTask();
            assertTrue(true);
        } catch (IllegalStateException e) {
            assertTrue(false);
        }
    }

    public void testDispatchMain() {
        final Tasker.ErrorHandler handler = new Tasker.ErrorHandler() {
            @Override
            public void onExceptionHandled(Task task, Throwable throwable) {
                assertEquals(Looper.getMainLooper(), Looper.myLooper());
            }
        };
        Tasker.init(handler, true);
        Tasker.execute(new TaskWithException());
    }

    public void testDispatchCaller() {
        final Tasker.ErrorHandler handler = new Tasker.ErrorHandler() {
            @Override
            public void onExceptionHandled(Task task, Throwable throwable) {
                assertNotSame(Looper.getMainLooper(), Looper.myLooper());
            }
        };
        Tasker.init(handler, false);
        Tasker.execute(new TaskWithException());
    }

    public void testExecuteSimple() {
        Tasker.init();
        assertTrue(Tasker.execute(new SleepTask(0L)));
    }

    public void testDuplicateReplace() {
        Tasker.init();
        final Task t1 = new SleepTask(2000L);
        final Task t2 = new SleepTask(0L);
        assertTrue(Tasker.execute(t1, "tag", DuplicateExecutionStrategy.REPLACE));
        assertTrue(Tasker.execute(t2, "tag", DuplicateExecutionStrategy.REPLACE));
        assertTrue(t1.isCancelled());
    }

    public void testDuplicateDoNotExecute() {
        Tasker.init();
        final Task t1 = new SleepTask(2000L);
        final Task t2 = new SleepTask(2000L);
        assertTrue(Tasker.execute(t1, "tag", DuplicateExecutionStrategy.DO_NOT_EXECUTE));
        assertFalse(Tasker.execute(t2, "tag", DuplicateExecutionStrategy.DO_NOT_EXECUTE));
        assertFalse(t2.isStarted());
    }

    public void testTaskDispatchMain() {
        Tasker.init();
        final Task task = new TaskWithDispatch(true, new AssertLooper(true));
        Tasker.execute(task);
    }

    public void testTaskDispatchCaller() {
        Tasker.init();
        final Task task = new TaskWithDispatch(false, new AssertLooper(false));
        Tasker.execute(task);
    }

    public void testTaskExceptionDispatchMain() {
        Tasker.init();
        final Task task = new TaskWithDispatchException(true, new AssertLooper(true));
        Tasker.execute(task);
    }

    public void testTaskExceptionDispatchCaller() {
        Tasker.init();
        final Task task = new TaskWithDispatchException(false, new AssertLooper(false));
        Tasker.execute(task);
    }

    public void testRunning() {
        Tasker.init();
        final String tag = "tag";
        final Task task = new TaskWithDispatch(true, new Apply<Void>() {
            @Override
            public void apply(Void aVoid) {
                assertFalse(Tasker.hasRunningTask(tag));
            }
        });
        Tasker.execute(task, tag);
        assertTrue(Tasker.hasRunningTask(tag));
    }

    private class NonStaticClass extends Task {

        @Override
        protected void execute() {

        }
    }

    private static class SimpleTask extends Task {

        @Override
        protected void execute() {

        }
    }

    private static class TaskWithException extends Task {

        @Override
        protected void execute() {
            throw new RuntimeException();
        }
    }

    private static class SleepTask extends Task {

        private final long time;

        SleepTask(long time) {
            this.time = time;
        }

        @Override
        protected void execute() {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {

            }
        }
    }

    private static class TaskWithDispatch extends Task {

        private final Apply<Void> mApply;

        public TaskWithDispatch(boolean isMain, Apply<Void> apply) {
            this.mApply = apply;
            setIsDispatchMainThread(isMain);
        }

        @Override
        protected void execute() {

        }

        @Override
        protected void onFinally() {
            mApply.apply(null);
        }
    }

    private static class TaskWithDispatchException extends Task {

        private final Apply<Void> mApply;

        private TaskWithDispatchException(boolean isMain, Apply<Void> apply) {
            mApply = apply;
            setIsDispatchMainThread(isMain);
        }

        @Override
        protected void execute() {
            throw new RuntimeException("testing");
        }

        @Override
        protected void onException(Throwable t) {
            mApply.apply(null);
        }
    }

    private interface Apply<T> {
        void apply(T t);
    }

    private static class AssertLooper implements Apply<Void> {

        private final boolean isMain;

        private AssertLooper(boolean isMain) {
            this.isMain = isMain;
        }

        @Override
        public void apply(Void aVoid) {
            if (isMain) {
                assertEquals(Looper.getMainLooper(), Looper.myLooper());
            } else {
                assertNotSame(Looper.getMainLooper(), Looper.myLooper());
            }
        }
    }
}
