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
 * A facade for handling logging calls.
 */
public class Wood implements Tree {

//    private final static String ACCURATETIME = "MM-dd_HH-mm-ss-SSS";
    private final static String BRIEFTIME = "MM-dd_HH-mm";
    private final static int MAX_LOG_LENGTH = 2048;

    private final static int VERBOSE = Level.V.Priority();
    private final static int DEBUG = Level.D.Priority();
    private final static int WARN = Level.W.Priority();
    private final static int INFO = Level.I.Priority();
    private final static int ERROR = Level.E.Priority();
    private final static int WTF = Level.A.Priority();

    private final static int ALL = Level.ALL.ordinal();
    private final static int V = Level.V.ordinal();
    private final static int D = Level.D.ordinal();
    private final static int W = Level.W.ordinal();
    private final static int I = Level.I.ordinal();
    private final static int E = Level.E.ordinal();
    private final static int A = Level.A.ordinal();
    private final static int S = Level.S.ordinal();

    /**
     * Logging policy that should be applied in order to control
     */
    private Spec MemoSpec = null;

    /**
     * Logging policy that should be applied in order to control
     */
    private boolean[] Valves = {false, false, false, true, true, true, true};

    private Disposable Disposable = null;

    private MemoThread MemoThread = null;

    private Level miniLevel = Level.W;

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
        if (spec.Filters != null) {
            for (Level filter : spec.Filters) {
                Valves[filter.ordinal()] = true;

                if(miniLevel.Priority() > filter.Priority()) {
                    miniLevel = filter;
                }
            }
        }

        Level level = (spec.Level != null ? spec.Level : miniLevel);

        for (int i = level.ordinal(), n = Valves.length; i < n; i++) {
            Valves[i] = true;
        }

        MemoSpec = spec;
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
     * Log verbose exception and message with optional format args.
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
     * Log debug exception and message with optional format args.
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
            throw new AssertionError(format(message, args));
        }
    }

    /**
     * Log an assert exception and message with optional format args.
     */
    @Override
    public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        if (Valves[A]) {
            throw new AssertionError(format(message, args), t);
        }
    }

    private String format(String message, Object... args) {
        if (args.length > 0) {
            try {
                message = String.format(message, args);
            } catch (IllegalFormatException e) {
                message = message + "(Args are not formative.)" ;
            }
        }

        return message;
    }

    private String truncateNames(@NonNull String name) {
        int MAXLEN = 8;
        int l = name.length();

        return name.substring(0, l > MAXLEN ? MAXLEN + 1 : l);
    }

    /**
     * Get tag ready and Convert message & args to log string.
     *
     * @param priority The priority/type of this log message
     * @param t        An exception to log.
     * @param message  The message you would like logged.
     * @param args     Arguments used to format the message string.
     */
    private void log(int priority, Throwable t, @NonNull String message, Object... args) {
        String text = format(message, args);

        if (t != null) {
            StringBuilder tb = new StringBuilder(text);
            String stacktrace = Tools.serializeException(t);
            tb.append("\n").append(stacktrace);
            text = tb.toString();
        }

        Milieu m = Timber.get();
        if (m == null) {
            Timber.e("Internal failure, could not get Milieu.");
            return;
        }

        String tag;
        if (m.what != null)
            tag = m.what;
        else if (MemoSpec == null)
            tag = m.who;
        else {
            // "[Class]@[Method]:[Thread]"
            StringBuilder tb = new StringBuilder();
            if (MemoSpec.Class != null && m.who.contains(MemoSpec.Class)) {
                tb.append('_').append(truncateNames(MemoSpec.Class)).append('_');
            } else
                tb.append(m.who);

            if (MemoSpec.Method != null && m.where.contains(MemoSpec.Method))
                tb.append('@').append('_').append(truncateNames(MemoSpec.Method)).append('_');

            if (MemoSpec.Thread != null && m.thread.contains(MemoSpec.Thread))
                tb.append(':').append('_').append(truncateNames(MemoSpec.Thread)).append('_');

            tag = tb.toString();
        }

        if (text.length() < MAX_LOG_LENGTH) {
            Log.println(priority, tag, text);
        } else {
            println(priority, tag, text);
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

                Log.println(priority, tag, part);
                i = end;
            } while (i < newline);
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
                        if (makeStore(storedir)) {
                            startMemo(storedir);
                        }

                        Disposable = null;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Disposable = null;
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

    private boolean makeStore(@NonNull String storedir) {
        try {
            Tools.makeDirectory(storedir);
        } catch (IOException e) {
            Timber.e(e, "Failed when creating log directory. Abort dumping.");
            return false;
        }

        return true;
    }

    private void startMemo(@NonNull String storedir) {
        if (MemoSpec == null) {
            return;
        }

        if (MemoThread == null) {
            MemoThread = new MemoThread(storedir);
        }

        // Thread is null by default
        MemoThread.startThread(miniLevel, MemoSpec);
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

        String line = reader.readLine();

        while(line != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }

            line = reader.readLine();
        }

        throw new AssertionError("Can not invoke ps call, using pid instead.");
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

        private int Pid;

        private Spec MemoSpec;

        private String Store;

        /**
         * Logging Proc that should be applied in order to control
         */
        volatile private Process Proc = null;

        volatile Pattern MatchPattern = null;


        /**
         * Nothing to write with logging level WTF
         */
        private BufferedWriter[] Writers = new BufferedWriter[A];


        MemoThread(@NonNull String storedir) {
            Store = storedir;
        }

        void startThread(@NonNull Level level, @NonNull Spec spec) {
            if (MemoSpec != null)
                return;

            MemoSpec = new Spec();

            MemoSpec.Level = level;

            if (spec.Class != null)
                MemoSpec.Class = "\\w*" + truncateNames(spec.Class) + "\\w*";
            else
                MemoSpec.Class = "\\w+";

            if (spec.Method != null)
                MemoSpec.Method = "@\\w*" + truncateNames(spec.Method) + "\\w*";
            else
                MemoSpec.Method = "";

            if (spec.Thread != null)
                MemoSpec.Thread = ":\\w*" + truncateNames(spec.Thread) + "\\w*";
            else
                MemoSpec.Thread = "";

            Pid = Tools.getHostProcessId();

            MatchPattern = buildPattern(String.valueOf(Pid),
                    MemoSpec.Class + MemoSpec.Method + MemoSpec.Thread);

            String cli = buildCliCommand(MemoSpec.Level, Pid);
            try {
                Proc = Runtime.getRuntime().exec(cli);
                start();
            } catch (IOException e) {
                Timber.e(e, "Run cli failed: %s", cli);
            }
        }

        void stopThread() {
            interrupt();
            if (Proc != null) {
                Proc.destroy();
            }
            Proc = null;
        }

        @Override
        public void run() {

            createWriters();

            try {
                InputStream input = Proc.getInputStream();
                BufferedReader mReader = new BufferedReader(new InputStreamReader(input), 4096);

                while (!isInterrupted()) {
                    String line = mReader.readLine();
                    if (line == null) {
                        try {
                            /*
                             * Sleep is the corner case, the memo thread should be always waiting
                             * process output stream in normal case.
                             */
                            sleep(600);
                        } catch (InterruptedException e) {
                            interrupt();
                        }
                        continue;
                    }

                    writeLines(MatchPattern, line);
                }
            } catch (IOException e) {
                /*
                 * Error reading in the logcat process' output stream, this is usually caused by
                 * process being destroyed. Here just discard the exception and close file output.
                 * CAN'T do any log since the tree have been uprooted.
                 */
            } finally {
                releaseWriters();
            }
        }


        private String buildCliCommand(@NonNull Level level, int pid) {
            return String.format("logcat --pid=%s -v threadtime *:%s",
                    String.valueOf(pid), level.name().toUpperCase());
        }

        private Pattern buildPattern(@NonNull String pid, @NonNull String tag) {
            String template = "\\b\\s+%s\\s+\\d+\\s+([VDWIEA])\\s+\\b%s";
            return Pattern.compile(
                    String.format(template, pid, tag));
        }

        private void writeLines(Pattern pattern, String line) {
            Matcher matcher = pattern.matcher(line);
            try {
                Writers[ALL].write(line);
                Writers[ALL].write('\n');

                if (!matcher.find()) {
                    return;
                }

                try {
                    int i = Level.valueOf(matcher.group(1)).ordinal();
                    if(Writers[i] != null) {
                        Writers[i].write(line);
                        Writers[i].write('\n');
                    }
                } catch (IllegalArgumentException e) {
                    Timber.e(e, "Level parsing error: <%s>", line);
                }
            } catch (IOException e) {
                Timber.e(e, "I/O Stream Error. <%s>", line);
            }
        }

        private void createWriters() {
            String paper = "";

            ArrayList<Level> filters = new ArrayList<>();
            if (Wood.this.MemoSpec != null && Wood.this.MemoSpec.Filters != null) {
                filters.addAll(Arrays.asList(Wood.this.MemoSpec.Filters));
            }

            filters.add(Level.ALL);

            for (Level level : filters) {
                /*
                 * Nothing to write with logging level WTF
                 */
                if (level.Priority() >= WTF) {
                    continue;
                }

                try {
                    paper = generatePaperName(Store, level.name());
                    File file = new File(paper);
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                    Writers[level.ordinal()] = writer;
                } catch (FileNotFoundException e) {
                    Timber.e(e, "Fail opening file: %s", paper);
                }
            }
        }

        private void releaseWriters() {
            for (BufferedWriter writer : Writers) {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        Timber.e(e, "Fail to close writer.");
                    }
                }
            }
        }
    }
}

