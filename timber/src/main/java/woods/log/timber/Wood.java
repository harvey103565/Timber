package woods.log.timber;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;




/**
 * logcat
 * Usage: logcat [options] [filterspecs]
 * options include:
 * -s              Set default filter to silent.
 * Like specifying filterspec '*:s'
 * -f <filename>   Log to file. Default to stdout
 * -r [<kbytes>]   Rotate log every kbytes. (16 if unspecified). Requires -f
 * -n <count>      Sets max number of rotated logs to <count>, default 4
 * -v <format>     Sets the log print format, where <format> is one of:
 * [brief, process, tag, thread, raw, time, threadtime, long]
 * -c              clear (flush) the entire log and exit
 * -d              dump the log and then exit (don't block)
 * -t <count>      print only the most recent <count> lines (implies -d)
 * -g              get the size of the log's ring buffer and exit
 * -b <buffer>     Request alternate ring buffer, 'main', 'system', 'radio'
 * or 'events'. Multiple -b parameters are allowed and the
 * results are interleaved. The default is -b main -b system.
 * -B              output the log in binary
 * <p>
 * filterspecs are packagename series of <tag>[:priority]
 * <p>
 * where <tag> is packagename log component tag (or * for all) and priority is:
 * V    Verbose
 * D    Debug
 * I    Info
 * W    Warn
 * E    Error
 * F    Fatal
 * S    Silent (supress all output)
 * <p>
 * '*' means '*:d' and <tag> by itself means <tag>:v
 * <p>
 * If not specified on the commandline, filterspec is set from ANDROID_LOG_TAGS.
 * If no filterspec is found, filter defaults to '*:I'
 * <p>
 * If not specified with -v, format is set from ANDROID_PRINTF_LOG
 * or defaults to "brief"
 */


/**
 * A facade for handling logging calls.
 */
public class Wood implements Tree {

    private final static String ACCURATETIME = "MM-dd_HH-mm-ss-SSS";
    private final static String BRIEFTIME = "MM-dd_HH-mm";
    private final static int MAX_LOG_LENGTH = 2048;

    private final static int VERBOSE = Level.V.ordinal();
    private final static int DEBUG = Level.D.ordinal();
    private final static int WARN = Level.W.ordinal();
    private final static int INFO = Level.I.ordinal();
    private final static int ERROR = Level.E.ordinal();
    private final static int WTF = Level.A.ordinal();
    private final static int ALL = Level.S.ordinal();

    /**
     * Logging policy that should be applied in order to control
     */
    private Spec Spec = null;

    /**
     * Logging policy that should be applied in order to control
     */
    private boolean[] Switches = new boolean[ALL];

    /**
     * Logging policy that should be applied in order to control
     */
    private Process[] Processes = new Process[ALL + 1];

    /**
     * Called when tree is added into forest.
     */
    @Override
    public void plant() {
        launchWorker();
    }

    /**
     * Called when tree is removed from forest.
     */
    @Override
    public void uproot() {
        for (Process process : Processes) {
            if (process != null) {
                process.destroy();
            }
        }
    }

    /**
     * Apply notation to the tree
     */
    @Override
    public void pin(@NonNull Spec spec) {
        if (spec.Level != null) {
            applyLoggingLevel(spec.Level);
        } else {
            applyLoggingLevel(Level.W);
        }

        if (spec.Filters != null) {
            applyFilters(spec.Filters);
        }

        Spec = spec;
    }

    /**
     * A view into Timber's planted trees as packagename tree itself. This can be used for injecting packagename logger
     * instance rather than using static methods or to facilitate testing.
     */
    @Override
    public Tree asTree() {
        return this;
    }

    /**
     * Log verbose message with optional format args.
     */
    @Override
    public void v(@NonNull String message, Object... args) {
        if (Switches[VERBOSE]) {
            log(VERBOSE, null, message, args);
        }
    }

    /**
     * Log verbose exception and packagename message with optional format args.
     */
    @Override
    public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[VERBOSE]) {
            log(VERBOSE, t, message, args);
        }
    }

    /**
     * Log debug message with optional format args.
     */
    @Override
    public void d(@NonNull String message, Object... args) {
        if (Switches[DEBUG]) {
            log(DEBUG, null, message, args);
        }
    }

    /**
     * Log debug exception and packagename message with optional format args.
     */
    @Override
    public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[DEBUG]) {
            log(DEBUG, t, message, args);
        }
    }

    /**
     * Log an info message with optional format args.
     */
    @Override
    public void i(@NonNull String message, Object... args) {
        if (Switches[INFO]) {
            log(INFO, null, message, args);
        }
    }

    /**
     * Log an info exception and message with optional format args.
     */
    @Override
    public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[INFO]) {
            log(INFO, t, message, args);
        }
    }

    /**
     * Log warning message with optional format args.
     */
    @Override
    public void w(@NonNull String message, Object... args) {
        if (Switches[WARN]) {
            log(WARN, null, message, args);
        }
    }

    /**
     * Log warning exception and message with optional format args.
     */
    @Override
    public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[WARN]) {
            log(WARN, t, message, args);
        }
    }

    /**
     * Log an error message with optional format args.
     */
    @Override
    public void e(@NonNull String message, Object... args) {
        if (Switches[ERROR]) {
            log(ERROR, null, message, args);
        }
    }

    /**
     * Log an error exception and message with optional format args.
     */
    @Override
    public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[ERROR]) {
            log(ERROR, t, message, args);
        }
    }

    /**
     * Log an assert message with optional format args.
     */
    @Override
    public void wtf(@NonNull String message, Object... args) {
        if (Switches[WTF]) {
            log(WTF, null, message, args);
        }
    }

    /**
     * Log an assert exception and message with optional format args.
     */
    @Override
    public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Switches[WTF]) {
            log(WTF, t, message, args);
        }
    }

    /**
     * Get tag ready and Convert message & args to log string.
     */
    private void log(int priority, Throwable t, @NonNull String message, Object... args) {
        if (args.length > 0) {
            try {
                message = String.format(message, args);
            } catch (IllegalFormatException e) {
                message = message + "(Args aren't formative.)" ;
            }
        }

        Milieu milieu = Timber.get();

        log(priority, milieu.what, message, t);
    }

    /**
     * The function that performs actual logging action.
     *
     * @param priority The priority/type of this log message
     * @param tag      Used to identify the source of packagename log message.  It usually identifies
     *                 the class or activity where the log call occurs.
     * @param message  The message you would like logged.
     * @param t        An exception to log.
     */
    public void log(int priority, String tag, String message, Throwable t) {
        StringBuilder ms = new StringBuilder(message);
        if (t != null) {
            String stacktrace = Tools.serializeException(t);
            ms.append("\n").append(stacktrace);
        }

        if (ms.length() < MAX_LOG_LENGTH) {
            Log.println(priority == WTF ? Log.ERROR : priority, tag, ms.toString());
        } else {
            println(priority, tag, ms.toString());
        }
    }

    /**
     * Break up {@code message} into maximum-length chunks (if needed) and send to either
     * {@link Log#println(int, String, String) Log.println()} or
     * {@link Log#wtf(String, String) Log.wtf()} for logging.
     * <p>
     * {@inheritDoc}
     */
    private void println(int priority, String tag, String message) {
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);

                Log.println(priority == WTF ? Log.ERROR : priority, tag, part);
                i = end;
            } while (i < newline);
        }
    }

    private void applyFilters(Level[] filters) {
        for (Level f : filters) {
            Switches[f.ordinal()] = true;
        }
    }

    private void applyLoggingLevel(Level l) {
        int k = l.ordinal();

        for (int i = 0; i < k; i++) {
            Switches[i] = false;
        }
        for (int i = k, n = Switches.length; i < n; i++) {
            Switches[i] = true;
        }
    }

    private void launchWorker() {
        Single.just(android.os.Process.myPid())
                .observeOn(Schedulers.io())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer pid) throws Exception {
                        return matchPackageName(pid);
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String pkgname) throws Exception {
                        return createStore(pkgname);
                    }
                })
                .doOnSuccess(new Consumer<String>() {
                    @Override
                    public void accept(String storedir) throws Exception {
                        clearStore(storedir);
                    }
                })
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String storedir) {
                        startWriter(storedir);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private String createStore(@NonNull String store) {
        StringBuilder pathbuilder = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            pathbuilder.append(Environment.getExternalStorageDirectory().getPath())
                    .append(File.separator).append("Android")
                    .append(File.separator).append("data");
        } else {
            pathbuilder.append(Environment.getDataDirectory().getPath())
                    .append(File.separator).append("data");
        }

        return pathbuilder.append(File.separator).append(store).toString();
    }

    private void clearStore(@NonNull String storedir) {
        try {
            Tools.makeDirectory(storedir);
        } catch (IOException e) {
            // TODO: Can not start work

            Timber.e(e, "Failed when creating log directory. Abort dumping.");
        }
    }

    private String buildCliCommand(@NonNull String outfile, int tid, String level, String clsname) {
        StringBuilder cb = new StringBuilder("logcat -v threadtime | grep \"")
                .append(Tools.getHostProcessId());

        if (tid != -1) {
            cb.append(tid);
        } else {
            cb.append(".*");
        }

        if (level != null) {
            cb.append(level);
        }

        if (clsname != null) {
            cb.append(" ").append(clsname);
        }

        cb.append("\" | tee ").append(outfile);

        return cb.toString();
    }

    private void startWriter(String storedir) {

        String memo_cli = "[No definition]";
        try {
            String paper = generatePaperName(storedir, "all");
            memo_cli = buildCliCommand(paper, -1, Spec.Level.name().toUpperCase(), null);
            Processes[ALL] = Runtime.getRuntime().exec(memo_cli);

            for (Level level : Spec.Filters) {
                paper = generatePaperName(storedir, level.name());
                memo_cli = buildCliCommand(paper, -1, level.name().toUpperCase(), Spec.Class);
            }
        } catch (IOException e) {
            Timber.e(e, "Fail to run command: %s.", memo_cli);
        }
    }

    private String matchPackageName(@NonNull Integer id) throws Exception {
        final Pattern pattern =
                Pattern.compile("\\b([a-z][a-z0-9_]*(?:\\.[a-z0-9_]+)+[0-9a-z_])\\b");

        String ps = "ps " + id;

        Process process = Runtime.getRuntime().exec(ps);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()), 1024);

        reader.readLine();
        String line = reader.readLine();

        if (line == null || line.isEmpty()) {
            Timber.e("Can not invoke ps call, using pid instead.");
            return ps;
        }
        Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) {
            Timber.e("No package name found, using pid instead.");
            return ps;
        }
        ps = matcher.group(1);
        if (ps == null) {
            throw new AssertionError("Could not find packagename.");
        }

        return ps;
    }

    private String generatePaperName(@NonNull String path, @NonNull String options) {
        StringBuilder full_name = new StringBuilder(path);
        SimpleDateFormat df = new SimpleDateFormat(BRIEFTIME, Locale.CHINA);

        full_name.append(File.separator)
                .append(df.format(System.currentTimeMillis()))
                .append("_Logs-")
                .append(options)
                .append(".log");

        return full_name.toString();
    }

}

