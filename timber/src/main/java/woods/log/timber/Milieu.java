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
     * Caller method<File Line: #>
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

    /**
     * Caller thread
     */
    public String thread;

    /**
     * Caller package
     */
    public String pkgname;


    public Milieu(@NonNull StackTraceElement trace, String tag) {
        when = dateFormat.format(System.currentTimeMillis());

        where = String.format(Locale.US,"%s<%s line: %d>", trace.getMethodName(),
                trace.getFileName(), trace.getLineNumber());

        who = Tools.getClassNameFromStack(trace);

        what = (tag == null ? who : tag);

        pkgname = Tools.getPackageNameFromStack(trace);

        thread = Thread.currentThread().getName();
    }

    public void bind(@NonNull Level level, Throwable thr, String trd) {
        how = level;
        why = thr;
        thread = trd;
    }
}
