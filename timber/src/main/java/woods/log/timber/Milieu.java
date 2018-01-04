package woods.log.timber;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Harvey bv on 2017/11/28.
 */

public class Milieu {

    private static final String ACCURATETIME = "MM-dd HH-mm-ss.SSS";

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);

    /**
     * Caller class
     */
    public String who;

    /**
     * Call at <File Line: #>
     */
    public String where;

    /**
     * Tag, what kind of logging, or who<method>
     */
    public String what;

    /**
     * Date-Time
     */
    public String when;

    /**
     * Exception, if there is
     */
    public Throwable why;

    /**
     * Logging level
     */
    public Level how;

    /**
     * Caller thread
     */
    public String thread;

    /**
     * Caller thread
     */
    public String pack;

    /**
     * Caller package
     */
    public String source;


    public Milieu(String tag) {
        final int STACK_INDEX = 4;

        StackTraceElement trace;
        StackTraceElement[] stack = new Throwable().getStackTrace();
        if (stack.length > STACK_INDEX) {
            trace = stack[STACK_INDEX];
        } else {
            Timber.wtf("Synthetic stacktrace didn't have enough elements: are you using proguard?");
            trace = stack[stack.length - 1];
        }

        when = dateFormat.format(System.currentTimeMillis());

        where = Tools.getMethodNameFromStack(trace);

        who = Tools.getClassNameFromStack(trace);

        what = tag;

        thread = Tools.getCurrentThreadName();

        pack = Tools.getPackageNameFromStack(trace);

        source = String.format("<%s:%s>", trace.getFileName(),
                String.valueOf(trace.getLineNumber()));
    }

    public void bind(@NonNull Level l, Throwable e) {
        how = l;
        why = e;
    }
}
