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
     * Caller thread
     */
    public String thread;

    /**
     * Caller class
     */
    public String who;

    /**
     * <Caller method>@File:Line
     */
    public String where;

    /**
     * Tag, what kind of logging
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


    public String packagename;


    public Milieu(@NonNull StackTraceElement trace) {
        who = Tools.getClassNameFromStack(trace);
        what = who;

        packagename = Tools.getPackageNameFromStack(trace);

        thread = Thread.currentThread().getName();

        where = String.format(Locale.US,"<%s>@%s:%d", trace.getMethodName(),
                trace.getFileName(), trace.getLineNumber());

        when = dateFormat.format(System.currentTimeMillis());
    }

    public Milieu(@NonNull String what) {
        this.what = what;

        SimpleDateFormat df = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);
        when = df.format(System.currentTimeMillis());
    }
}
