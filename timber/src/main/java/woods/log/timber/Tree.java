package woods.log.timber;

import android.support.annotation.NonNull;
import java.util.IllegalFormatException;


/**
 * A facade for handling logging calls.
 */
public abstract class Tree implements Wood {

    private boolean[] Switches = {false, false, true, true, true, true, false};

    protected final static int VERBOSE = Level.V.ordinal();

    protected final static int DEBUG = Level.D.ordinal();

    protected final static int WARN = Level.W.ordinal();

    protected final static int INFO = Level.I.ordinal();

    protected final static int ERROR = Level.E.ordinal();

    protected final static int WTF = Level.A.ordinal();


    /**
     * Logging policy that should be applied in order to control
     */
    protected Tip opTip = null;

    protected Probe envProbe = null;


    /**
     * Set tag for log message
     */
    @Override
    public Wood tag(String tag) {
        return this;
    }

    /**
     * Log a verbose message with optional format args.
     */
    @Override
    public void v(@NonNull String message, Object... args) {
        if (Switches[VERBOSE]) {
            prelog(VERBOSE, null, message, args);
        }
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    @Override
    public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[VERBOSE]) {
            prelog(VERBOSE, t, message, args);
        }
    }

    /**
     * Log a debug message with optional format args.
     */
    @Override
    public void d(@NonNull String message, Object... args) {
        if (Switches[DEBUG]) {
            prelog(DEBUG, null, message, args);
        }
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    @Override
    public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[DEBUG]) {
            prelog(DEBUG, t, message, args);
        }
    }

    /**
     * Log an info message with optional format args.
     */
    @Override
    public void i(@NonNull String message, Object... args) {
        if (Switches[INFO]) {
            prelog(INFO, null, message, args);
        }
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    @Override
    public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[INFO]) {
            prelog(INFO, t, message, args);
        }
    }

    /**
     * Log a warning message with optional format args.
     */
    @Override
    public void w(@NonNull String message, Object... args) {
        if (Switches[WARN]) {
            prelog(WARN, null, message, args);
        }
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    @Override
    public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[WARN]) {
            prelog(WARN, t, message, args);
        }
    }

    /**
     * Log an error message with optional format args.
     */
    @Override
    public void e(@NonNull String message, Object... args) {
        if (Switches[ERROR]) {
            prelog(ERROR, null, message, args);
        }
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    @Override
    public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ERROR]) {
            prelog(ERROR, t, message, args);
        }
    }

    /**
     * Log an assert message with optional format args.
     */
    @Override
    public void wtf(@NonNull String message, Object... args) {
        if (Switches[WTF]) {
            prelog(WTF, null, message, args);
        }
    }

    /**
     * Log an assert exception and a message with optional format args.
     */
    @Override
    public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[WTF]) {
            prelog(WTF, t, message, args);
        }
    }

    @Override
    public void onplant(Probe probe) {
        if (probe == null) {
            throw new AssertionError("Plant tree without valid environment probe.");
        }

        envProbe = probe;
    }

    @Override
    public void onuproot() {
    }

    @Override
    public void pin(String notes) {
        opTip = Tools.parseTipString(notes);

        if (opTip == null) {
            return;
        }

        if (opTip.Level != null) {
            applyLoggingLevel(opTip.Level);
        }

        if (opTip.Filters != null) {
            applyFilters(opTip.Filters);
        }
    }

    /**
     * Get tag ready and Convert message & args to log string.
     */
    private void prelog(int priority, Throwable t, @NonNull String message, Object... args) {
        if (!isLoggable()) {
            return;
        }

        String tag = (envProbe == null ? "Timber-No-Tag" : envProbe.getTag());

        if (args.length > 0) {
            try {
                message = String.format(message, args);
            } catch (IllegalFormatException e) {
                message = "Discard log due to illegal formatting.";
            }
        }

        log(priority, tag, message, t);
    }

    /**
     * The function that performs actual logging action.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of a log message.  It usually identifies
     *                 the class or activity where the log call occurs.
     * @param message  The message you would like logged.
     * @param t        An exception to log.
     */
    abstract public void log(int priority, String tag, String message, Throwable t);

    protected void applyFilters(Level[] filters) {
        for (Level f : filters) {
            Switches[f.ordinal()] = true;
        }
    }

    protected void applyLoggingLevel(Level l) {
        int k = l.ordinal();

        for (int i = 0; i < k; i++) {
            Switches[i] = false;
        }
        for (int i = k, n = Switches.length; i < n; i++) {
            Switches[i] = true;
        }
    }

    private boolean isLoggable() {
        if (opTip == null || envProbe == null) {
            return true;
        }

        String t = envProbe.getThreadName();
        if (opTip.Thread != null && !opTip.Thread.equals(t)) {
            return false;
        }

        String cls = envProbe.getClassName();
        return !(opTip.Class != null && !opTip.Class.equals(cls));
    }
}

