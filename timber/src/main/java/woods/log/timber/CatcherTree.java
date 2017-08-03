package woods.log.timber;

import android.support.annotation.NonNull;

/**
 * A tree catches un-handle exception and monitor memory leak
 */

public class CatcherTree implements Tree {

    // Save input policy here
    private Policy Policy = null;

    // Save system default UncaughtException here
    private Thread.UncaughtExceptionHandler defaultHandler = null;

    @Override
    public Tree tag(String tag) {
        return this;
    }

    @Override
    public Tree policy(Policy policy) {
        Policy = policy;
        return this;
    }

    @Override
    public Tree prober(Prober prober) {
        return this;
    }

    @Override
    public void v(@NonNull String message, Object... args) {
    }

    @Override
    public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
    }

    @Override
    public void d(@NonNull String message, Object... args) {
    }

    @Override
    public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
    }

    @Override
    public void i(@NonNull String message, Object... args) {
    }

    @Override
    public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
    }

    @Override
    public void w(@NonNull String message, Object... args) {
    }

    @Override
    public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
    }

    @Override
    public void e(@NonNull String message, Object... args) {
    }

    @Override
    public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
    }

    @Override
    public void wtf(@NonNull String message, Object... args) {
    }

    @Override
    public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
    }

    @Override
    public void plant() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    public void uproot() {
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
    }


    private final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            if (throwable != null) {
                handleException(throwable);
            }
        }

        private void handleException(Throwable throwable) {
            if (Policy == null) {
                return;
            }

            String t = Tools.getCurrentThreadName();
            if (Policy.Thread != null && !Policy.Thread.equals(t)) {
                return;
            }

            StackTraceElement s = throwable.getStackTrace()[0];
            String cls = Tools.getClassNameFromStack(s);
            if (Policy.Class != null && !Policy.Class.equals(cls)) {
                return;
            }

            String p = Tools.getPackageNameFromStack(s);
            if (Policy.Package != null && !Policy.Package.equals(p)) {
                return;
            }

            Timber.wtf(throwable, "Unhandled Exception.");
        }
    };
}
