package woods.log.timber;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
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
 * filterspecs are pkgname series of <tag>[:priority]
 * <p>
 * where <tag> is pkgname log component tag (or * for all) and priority is:
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

    private final static int VERBOSE = Level.V.Priority();
    private final static int DEBUG = Level.D.Priority();
    private final static int WARN = Level.W.Priority();
    private final static int INFO = Level.I.Priority();
    private final static int ERROR = Level.E.Priority();
    private final static int WTF = Level.A.Priority();
    private final static int SUP = Level.S.Priority();

    private final static int ALL = Level.ALL.ordinal();
    private final static int V = Level.V.ordinal();
    private final static int D = Level.D.ordinal();
    private final static int W = Level.W.ordinal();
    private final static int I = Level.I.ordinal();
    private final static int E = Level.E.ordinal();
    private final static int A = Level.A.ordinal();
    /**
     * Logging policy that should be applied in order to control
     */
    private Spec Spec = null;

    /**
     * Logging policy that should be applied in order to control
     */
    private boolean[] Valves = new boolean[SUP];


    private Disposable Disposable = null;


    private MemoThread MemoThread = null;

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
        if (Disposable != null) {
            if (!Disposable.isDisposed()) {
                Disposable.dispose();
            }
        }

        stopMemo();
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
     * A view into Timber's planted trees as pkgname tree itself. This can be used for injecting pkgname logger
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
        if (Valves[V]) {
            log(VERBOSE, null, message, args);
        }
    }

    /**
     * Log verbose exception and pkgname message with optional format args.
     */
    @Override
    public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[V]) {
            log(VERBOSE, t, message, args);
        }
    }

    /**
     * Log debug message with optional format args.
     */
    @Override
    public void d(@NonNull String message, Object... args) {
        if (Valves[D]) {
            log(DEBUG, null, message, args);
        }
    }

    /**
     * Log debug exception and pkgname message with optional format args.
     */
    @Override
    public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[D]) {
            log(DEBUG, t, message, args);
        }
    }

    /**
     * Log an info message with optional format args.
     */
    @Override
    public void i(@NonNull String message, Object... args) {
        if (Valves[I]) {
            log(INFO, null, message, args);
        }
    }

    /**
     * Log an info exception and message with optional format args.
     */
    @Override
    public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[I]) {
            log(INFO, t, message, args);
        }
    }

    /**
     * Log warning message with optional format args.
     */
    @Override
    public void w(@NonNull String message, Object... args) {
        if (Valves[W]) {
            log(WARN, null, message, args);
        }
    }

    /**
     * Log warning exception and message with optional format args.
     */
    @Override
    public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[W]) {
            log(WARN, t, message, args);
        }
    }

    /**
     * Log an error message with optional format args.
     */
    @Override
    public void e(@NonNull String message, Object... args) {
        if (Valves[E]) {
            log(ERROR, null, message, args);
        }
    }

    /**
     * Log an error exception and message with optional format args.
     */
    @Override
    public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[E]) {
            log(ERROR, t, message, args);
        }
    }

    /**
     * Log an assert message with optional format args.
     */
    @Override
    public void wtf(@NonNull String message, Object... args) {
        if (Valves[A]) {
            log(WTF, null, message, args);
        }
    }

    /**
     * Log an assert exception and message with optional format args.
     */
    @Override
    public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[A]) {
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
     * @param tag      Used to identify the source of pkgname log message.  It usually identifies
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
            Log.println(priority == WTF ? ERROR : priority, tag, ms.toString());
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

                Log.println(priority == WTF ? ERROR : priority, tag, part);
                i = end;
            } while (i < newline);
        }
    }

    private void applyFilters(Level[] filters) {
        for (Level f : filters) {
            Valves[f.ordinal()] = true;
        }
    }

    private void applyLoggingLevel(Level l) {
        int k = l.ordinal();

        for (int i = 0; i < k; i++) {
            Valves[i] = false;
        }
        for (int i = k, n = Valves.length; i < n; i++) {
            Valves[i] = true;
        }
    }

    private void launchWorker() {
        Single.just(android.os.Process.myPid())
                .observeOn(Schedulers.newThread())
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer pid) throws Exception {
                        return matchPackageName(pid);
                    }
                })
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String pkgname) throws Exception {
                        return sitingStore(pkgname);
                    }
                })
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Disposable = d;
                    }

                    @Override
                    public void onSuccess(String storedir) {
                        makeStore(storedir);
                        startMemo(storedir);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private String sitingStore(@NonNull String store) {
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

    private void makeStore(@NonNull String storedir) {
        try {
            Tools.makeDirectory(storedir);
        } catch (IOException e) {
            // TODO: Can not start work

            Timber.e(e, "Failed when creating log directory. Abort dumping.");
        }
    }

    private String buildCliCommand(int pid, @NonNull Level level) {
        StringBuilder cb = new StringBuilder("logcat -v thread *:")
                .append(level.name().toUpperCase())
                .append(" | grep ' ")
                .append(pid)
                .append(" '");

        return cb.toString();
    }

    private void startMemo(String storedir) {
        String memo_cli = buildCliCommand(Tools.getHostProcessId(), Spec.Level);
        MemoThread = new MemoThread(memo_cli, storedir);
        MemoThread.startThread();
    }

    private void stopMemo() {
        if (MemoThread != null) {
            MemoThread.stopThread();
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
            throw new AssertionError("Could not find pkgname.");
        }

        return ps;
    }

    private String generatePaperName(@NonNull String path, @NonNull String options) {
        StringBuilder name_builder = new StringBuilder(path);
        SimpleDateFormat df = new SimpleDateFormat(BRIEFTIME, Locale.CHINA);

        name_builder.append(File.separator)
                .append(df.format(System.currentTimeMillis()))
                .append("_Logs-")
                .append(options)
                .append(".log");

        return name_builder.toString();
    }

    private final class MemoThread extends Thread {

        private boolean _Running = false;

        private String _Cli;

        private String _StoreDir;

        /**
         * Logging process that should be applied in order to control
         */
        private BufferedWriter[] Writers = new BufferedWriter[SUP];


        MemoThread(String cli, String storedir) {
            _Cli = cli;
            _StoreDir = storedir;
        }

        void startThread() {
            boolean running;

            synchronized (this) {
                running = _Running;
                _Running = true;
            }

            if (!running) {
                start();
            }
        }

        void stopThread() {
            boolean running;

            synchronized (this) {
                running = _Running;
                _Running = false;
            }

            if (running) {
                interrupt();
            }
        }

        @Override
        public void run() {

            Pattern pattern = Pattern.compile("\\b \\d+ \\d+ ([VDWIEA]) \\b");
            try {
                createWriters();

                Process process = Runtime.getRuntime().exec(_Cli);
                InputStream input = process.getInputStream();
                BufferedReader mReader = new BufferedReader(new InputStreamReader(input), 4096);

                try  {
                    while (_Running) {
                        String line = mReader.readLine();
                        if (line == null) {
                            sleep(1000);
                        } else {
                            Matcher matcher = pattern.matcher(line);
                            if (matcher != null) {
                                Level level = Level.valueOf(matcher.group(1));
                                int i = level.ordinal();
                                if (Writers[i] != null) {
                                    Writers[i].write(line);
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    stopThread();
                    Timber.w(e, "Dumper Thread thread quit.");
                } finally {
                    releaseWriters();
                    process.destroy();
                }
            } catch (IOException e) {
                Timber.w(e, "I/O Stream Error: %s");
            }
        }

        private void createWriters() throws FileNotFoundException {
            String paper = "";

            ArrayList<Level> filters = new ArrayList<Level>(Arrays.asList(Spec.Filters));
            filters.add(Level.ALL);

            for (Level level : filters) {
                paper = generatePaperName(_StoreDir, level.name());
                File file = new File(paper);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                Writers[level.ordinal()] = writer;
            }
        }

        private void releaseWriters() throws IOException {
            for (BufferedWriter writer : Writers) {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            }
        }
    }
}

