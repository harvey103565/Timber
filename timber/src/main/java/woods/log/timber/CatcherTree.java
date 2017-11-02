package woods.log.timber;

/**
 * A tree catches un-handle exception and monitor memory leak
 */

public class CatcherTree implements Plant {

    private Tip opTip = null;

    private Thread.UncaughtExceptionHandler defaultHandler = null;

    @Override
    public void onplant(Probe probe) {
        if (opTip != null) {
            defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }
    }

    @Override
    public void onuproot() {
        Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
    }

    @Override
    public void pin(String notes) {
        opTip = Tools.parseTipString(notes);
    }


    private final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable throwable) {
            if (throwable != null) {
                handleException(throwable);
            }
        }

        private void handleException(Throwable throwable) {
            if (opTip == null) {
                return;
            }

            String t = Tools.getCurrentThreadName();
            if (opTip.Thread != null && !opTip.Thread.equals(t)) {
                return;
            }

            StackTraceElement s = throwable.getStackTrace()[0];
            String cls = Tools.getClassNameFromStack(s);
            if (opTip.Class != null && !opTip.Class.equals(cls)) {
                return;
            }

            Timber.wtf(throwable, "Unhandled Exception.");
        }
    };
}
