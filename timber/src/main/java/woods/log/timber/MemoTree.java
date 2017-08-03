package woods.log.timber;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;


/**
 * A tree to dump all logs of current process into one file('memo'), for persisting purpose.
 * Use {@link Echo} annotation to configure for simple.
 * It can writes each message into single file('Paper') as well. Use {@link Paper} annotation
 * for simple to configure.
 */


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
 * filterspecs are a series of <tag>[:priority]
 * <p>
 * where <tag> is a log component tag (or * for all) and priority is:
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

public class MemoTree extends LogTree {

    private static final String ACCURATETIME = "MM-dd HH-mm-ss.SSS";

    private static final String BRIEFTIME = "MM-dd HH-mm";

    private volatile String StoreDirectory = null;

    private Prober Prober = null;

    private Policy Policy = null;

    private MemoThread WorkerThread = null;


    private final class MemoThread extends Thread {

        private boolean _Running = false;

        public void startThread() {
            boolean running;

            synchronized (this) {
                running = _Running;
                _Running = true;
            }

            if (!running) {
                start();
            }
        }

        public void stopThread() {
            boolean running;

            synchronized (this) {
                running = _Running;
                _Running = false;
            }

            if (running) {
                interrupt();
            }
        }

        private String buildCommand(Policy policy) {
            StringBuilder cb = new StringBuilder("logcat --pid=")
                    .append(Tools.getHostProcessId())
                    .append(" -v thread");

            if (policy != null) {
                if (policy.Level != null) {
                    cb.append(" *:").append(policy.Level);
                }

                if (policy.Class != null) {
                    cb.append(" | grep ").append(policy.Class);
                }
            }

            return cb.toString();
        }

        @Override
        public void run() {
            String ffn = "";
            String cmd = buildCommand(Policy);
            try {
                Process Process = Runtime.getRuntime().exec(cmd);
                InputStream input = Process.getInputStream();
                BufferedReader mReader = new BufferedReader(new InputStreamReader(input), 4096);

                Tools.flatDirectory(StoreDirectory, Tools.MAX_HOURS_TO_KEEP);
                ffn = generateFullBookName(StoreDirectory, Policy.Level.name());
                File file = Tools.makeFile(ffn);

                SimpleDateFormat tf = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);

                FileOutputStream os = new FileOutputStream(file);
                OutputStreamWriter ow = new OutputStreamWriter(os);
                BufferedWriter writer = new BufferedWriter(ow);
                try {
                    String line;
                    while (_Running) {
                        line = mReader.readLine();
                        if (line == null) {
                            sleep(1000);
                        }

                        StringBuilder tb = new StringBuilder(
                                tf.format(System.currentTimeMillis()));
                        writer.write(tb.append(" ").append(line).append("\n")
                                .toString());
                    }
                } catch (InterruptedException e) {
                    Process.destroy();
                    stopThread();
                    Timber.w(e, "Worker thread quit.");
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                Timber.w(e, "I/O Stream Error: %s", ffn);
            }
        }
    }

    @Override
    public Tree policy(Policy policy) {
        super.policy(policy);

        Policy = policy;
        if (Policy.Level == null) {
            Policy.Level = Level.W;
        }

        return this;
    }

    @Override
    public Tree prober(Prober prober) {
        super.prober(prober);

        if (prober == null) {
            throw new AssertionError("Prober could not be null!");
        }
        Prober = prober;

        StoreDirectory = prober.getStoragePath();
        if (StoreDirectory == null) {
            Timber.e("No store directory detected.");
        }
        try {
            Tools.makeDirectory(StoreDirectory);
        } catch (IOException e) {
            applyLoggingLevel(Level.S);
        }

        tryWakingWorker();

        return this;
    }

    @Override
    public void log(int priority, String tag, String message, Throwable t) {
        if (StoreDirectory == null) {
            return;
        }

        String ffn = generateFullPaperName(StoreDirectory);
        File file = new File(ffn);

        FileOutputStream fos;
        try {
            if (file.createNewFile()) {
                fos = new FileOutputStream(file);
                try {
                    fos.write(message.getBytes());
                    if (t != null) {
                        fos.write("\n".getBytes());
                        fos.write(Tools.serializeException(t).getBytes());
                    }
                } finally {
                    fos.close();
                }
            }
        } catch (IOException e) {
            applyLoggingLevel(Level.S);
            Timber.w(e, "Output file Error: %s.", ffn);
        }
    }

    @Override
    public void plant() {
        tryWakingWorker();
    }

    @Override
    public void uproot() {
        stopWorkerWorking();
    }

    private void tryWakingWorker() {

        if (WorkerThread == null) {
            WorkerThread = new MemoThread();
        }

        if (Prober != null && StoreDirectory != null) {
            WorkerThread.startThread();
        }
    }

    private void stopWorkerWorking() {
        WorkerThread.stopThread();
    }

    private String generateFullPaperName(@NonNull String path) {
        StringBuilder ffnb = new StringBuilder(path);
        SimpleDateFormat df = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);

        ffnb = ffnb.append(File.separator);
        if (Policy != null && Policy.Catalog != null && !Policy.Catalog.isEmpty()) {
            ffnb = ffnb.append(Policy.Catalog).append(File.separator);
        }
        ffnb = ffnb.append(df.format(System.currentTimeMillis()));

        if (null != Prober) {
            ffnb.append(' ')
                    .append(Prober.getMethodName())
                    .append('-')
                    .append(Prober.getFileLine());
        }

        return ffnb.toString();
    }

    private String generateFullBookName(@NonNull String path, @NonNull String options) {
        StringBuilder ffn = new StringBuilder(path);
        SimpleDateFormat df = new SimpleDateFormat(BRIEFTIME, Locale.CHINA);

        ffn.append(File.separator)
                .append(df.format(System.currentTimeMillis()))
                .append(" Timber-Logs-")
                .append(options)
                .append(".log");

        return ffn.toString();
    }
}
