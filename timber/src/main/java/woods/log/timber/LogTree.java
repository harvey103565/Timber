package woods.log.timber;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.IllegalFormatException;


/**
 * A facade for handling logging calls.
 */
public abstract class LogTree implements Tree {

    protected boolean[] Switches = {false, false, true, true, true, true, false};

    protected final static int ONVERBOSE = 0;

    protected final static int ONDEBUG = 1;

    protected final static int ONWARN = 2;

    protected final static int ONINFO = 3;

    protected final static int ONERROR = 4;

    protected final static int ONWTF = 5;

    protected final static int SUPPRESS = 6;

    private ThreadLocal<String> Tag = new ThreadLocal<>();

    /**
     * Logging policy that should be applied in order to control
     */
    private Policy Policy = null;

    /**
     * Evironment prober used to gather running evironment information and make logs
     */
    private Prober Prober = null;


    /**
     * Set tag for log message
     */
    @Override
    public Tree tag(String tag) {
        Tag.set(tag);
        return this;
    }

    @Override
    public Tree policy(Policy policy) {
        if (Policy != null) {
            applyPolicy(policy);
        }

        Policy = policy;
        return this;
    }

    @Override
    public Tree prober(Prober prober) {
        if (prober == null) {
            throw new AssertionError("Prober could not be null!");
        }

        Prober = prober;
        return this;
    }

    /**
     * Log a verbose message with optional format args.
     */
    @Override
    public void v(@NonNull String message, Object... args) {
        if (Switches[ONVERBOSE]) {
            prelog(Log.VERBOSE, null, message, args);
        }
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    @Override
    public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ONVERBOSE]) {
            prelog(Log.VERBOSE, t, message, args);
        }
    }

    /**
     * Log a debug message with optional format args.
     */
    @Override
    public void d(@NonNull String message, Object... args) {
        if (Switches[ONDEBUG]) {
            prelog(Log.DEBUG, null, message, args);
        }
    }

    /**
     * Log a debug exception and a message with optional format args.
     */
    @Override
    public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ONDEBUG]) {
            prelog(Log.DEBUG, t, message, args);
        }
    }

    /**
     * Log an info message with optional format args.
     */
    @Override
    public void i(@NonNull String message, Object... args) {
        if (Switches[ONINFO]) {
            prelog(Log.INFO, null, message, args);
        }
    }

    /**
     * Log an info exception and a message with optional format args.
     */
    @Override
    public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ONINFO]) {
            prelog(Log.INFO, t, message, args);
        }
    }

    /**
     * Log a warning message with optional format args.
     */
    @Override
    public void w(@NonNull String message, Object... args) {
        if (Switches[ONWARN]) {
            prelog(Log.WARN, null, message, args);
        }
    }

    /**
     * Log a warning exception and a message with optional format args.
     */
    @Override
    public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ONWARN]) {
            prelog(Log.WARN, t, message, args);
        }
    }

    /**
     * Log an error message with optional format args.
     */
    @Override
    public void e(@NonNull String message, Object... args) {
        if (Switches[ONERROR]) {
            prelog(Log.ERROR, null, message, args);
        }
    }

    /**
     * Log an error exception and a message with optional format args.
     */
    @Override
    public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ONERROR]) {
            prelog(Log.ERROR, t, message, args);
        }
    }

    /**
     * Log an assert message with optional format args.
     */
    @Override
    public void wtf(@NonNull String message, Object... args) {
        if (Switches[ONWTF]) {
            prelog(Log.ASSERT, null, message, args);
        }
    }

    /**
     * Log an assert exception and a message with optional format args.
     */
    @Override
    public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ONWTF]) {
            prelog(Log.ASSERT, t, message, args);
        }
    }

    /**
     * Get tag ready and Convert message & args to log string.
     */
    private void prelog(int priority, Throwable t, @NonNull String message, Object... args) {

        if (!checkPolicy()) {
            return;
        }

        String tag = Tag.get();
        if (tag == null) {
            if (Prober != null) {
                tag = Prober.getClassName();
            } else {
                tag = "timber-no-tag";
            }
        } else {
            Tag.remove();
        }

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

    protected void applyPolicy(Policy policy) {
        if (policy.Level != null) {
            applyLoggingLevel(Policy.Level);
        }

        if (policy.Filters != null) {
            applyFilters(policy.Filters);
        }
    }

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

    private boolean checkPolicy() {
        if (Policy == null) {
            return false;
        }

        String t = Tools.getCurrentThreadName();
        if (Policy.Thread != null && !Policy.Thread.equals(t)) {
            return false;
        }

        String cls = Prober.getClassName();
        if (Policy.Class != null && !Policy.Class.equals(cls)) {
            return false;
        }

        String p = Prober.getPackageName();

        return !(Policy.Class != null && !Policy.Package.equals(p));
    }
}

