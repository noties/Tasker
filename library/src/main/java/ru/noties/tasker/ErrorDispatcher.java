package ru.noties.tasker;


import android.os.Handler;

/**
 * Created by Dimitry Ivanov on 03.08.2015.
 */
abstract class ErrorDispatcher {

    static ErrorDispatcher obtain(boolean isMainThread, Tasker.ErrorHandler errorHandler, Handler handler) {

        if (errorHandler == null) {
            return new NoOpDispatcher();
        }

        if (isMainThread) {
            return new MainThreadDispatcher(handler, errorHandler);
        }

        return new CallerThreadDispatcher(errorHandler);
    }

    abstract void dispatch(Task task, Throwable t);

    final Tasker.ErrorHandler mErrorHandler;

    ErrorDispatcher(Tasker.ErrorHandler errorHandler) {
        mErrorHandler = errorHandler;
    }

    private static class NoOpDispatcher extends ErrorDispatcher {

        NoOpDispatcher() {
            super(null);
        }

        @Override
        void dispatch(Task task, Throwable t) {

        }
    }

    private static class CallerThreadDispatcher extends ErrorDispatcher {

        CallerThreadDispatcher(Tasker.ErrorHandler errorHandler) {
            super(errorHandler);
        }

        @Override
        void dispatch(Task task, Throwable t) {
            mErrorHandler.onExceptionHandled(task, t);
        }
    }

    private static class MainThreadDispatcher extends ErrorDispatcher {

        private final Handler mHandler;

        private MainThreadDispatcher(Handler handler, Tasker.ErrorHandler errorHandler) {
            super(errorHandler);
            mHandler = handler;
        }

        @Override
        void dispatch(final Task task, final Throwable t) {
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    mErrorHandler.onExceptionHandled(task, t);
                }
            };
            mHandler.post(r);
        }
    }
}
