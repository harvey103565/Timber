package woods.log.timber;

import android.os.Environment;
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
import java.util.concurrent.ConcurrentLinkedQueue;


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

public class MemoTree extends Wood {

    private static final String ACCURATETIME = "MM-dd HH-mm-ss.SSS";

    private static final String BRIEFTIME = "MM-dd HH-mm";

    private static final Level[] levelArray = Level.values();
    private final ConcurrentLinkedQueue<String> messageList = new ConcurrentLinkedQueue<>();
    private volatile String storeDirectory = null;
    private DumperThread dumperThread = null;

    private WriterThread wirterThread = null;

    private boolean dumpEnabled = false;

    private boolean writeEnabled = false;

    private String catalogString = null;

    private String cliString = null;

    @Override
    public void log(int priority, String tag, String message, Throwable t) {
        if (storeDirectory == null) {
            return;
        }

        StringBuilder msb = new StringBuilder();

        if (priority < VERBOSE) {
            msb.append("Supress");
        } else if (priority > WTF) {
            msb.append("All");
        } else {
            msb.append(levelArray[priority].toString());
        }

        Milieu milieu = Timber.get();

        msb.append('-')
                .append(milieu.fileLine)
                .append(":\n");

        if (t != null) {
            msb.append(Tools.serializeException(t));
        }

        messageList.add(msb.toString());
    }

    @Override
    public void plant(@NonNull Tree tree) {
        super.plant(tree);

        if (tip != null && tip.Catalog != null) {
            createCatalog(tip.Catalog);
        } else {
            Milieu milieu = Timber.get();
            if (milieu.packageName != null) {
                createCatalog(milieu.packageName);
            } else {
                createCatalog("timber.unknown.package.default");
            }
        }

        tryWakingWorker();
    }

    @Override
    public void uproot(@NonNull Tree tree) {
        stopWorkerWorking();
    }

    @Override
    public void pin(@NonNull Tip tip) {
        super.pin(tip);

        if (tip != null) {
            if (tip.Level != null) {
                dumpEnabled = true;
                cliString = buildCommand(tip);
            }

            if (tip.Filters != null) {
                writeEnabled = true;
            }
        }
    }

    private void createCatalog(@NonNull String catalog) {
        StringBuilder pathbuilder = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            pathbuilder.append(Environment.getExternalStorageDirectory().getPath())
                    .append(File.separator).append("Android")
                    .append(File.separator).append("data");
        } else {
            pathbuilder.append(Environment.getDataDirectory().getPath())
                    .append(File.separator).append("data");
        }

        storeDirectory = pathbuilder.append(File.separator).append(catalog).toString();

        Tools.flatDirectory(storeDirectory, Tools.MAX_HOURS_TO_KEEP);
        try {
            Tools.makeDirectory(storeDirectory);
        } catch (IOException e) {
            dumpEnabled = false;
            writeEnabled = false;

            Timber.wtf(e, "Failed when creating log directory. Abort dumping.");
        }
    }

    private void tryWakingWorker() {

        if (storeDirectory == null) {
            return;
        }

        if (dumpEnabled) {
            if (dumperThread == null) {
                dumperThread = new DumperThread();
            }
            dumperThread.startThread();
        }

        if (writeEnabled) {
            if (wirterThread == null) {
                wirterThread = new WriterThread();
            }
            wirterThread.startThread();
        }
    }

    private void stopWorkerWorking() {
        dumperThread.stopThread();
    }

    private String buildCommand(@NonNull Tip tip) {
        StringBuilder cb = new StringBuilder("logcat --pid=")
                .append(Tools.getHostProcessId())
                .append(" -v thread");

        cb.append(" *:").append(tip.Level);

        if (tip.Class != null) {
            cb.append(" | grep ").append(tip.Class);
        }

        return cb.toString();
    }

    private String generateFullPaperName(@NonNull String path) {
        StringBuilder ffnb = new StringBuilder(path);
        SimpleDateFormat df = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);

        ffnb = ffnb.append(File.separator);
        if (catalogString != null) {
            ffnb = ffnb.append(catalogString).append(File.separator);
        }
        ffnb = ffnb.append(df.format(System.currentTimeMillis()));

        Milieu milieu = Timber.get();

        ffnb.append(' ')
                .append(milieu.callerMethodName)
                .append('-')
                .append(milieu.fileLine);

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

    private void writerLoop() {
        while (!messageList.isEmpty()) {
            String message = messageList.poll();

            String ffn = generateFullPaperName(storeDirectory);
            File file = new File(ffn);

            FileOutputStream fos;
            try {
                if (file.createNewFile()) {
                    fos = new FileOutputStream(file);
                    try {
                        fos.write(message.getBytes());
                    } finally {
                        fos.close();
                    }
                }
            } catch (IOException e) {
                applyLoggingLevel(Level.S);
                Timber.w(e, "Output file Error: %s.", ffn);
            }
        }
    }

    private class MemoThread extends Thread {
        boolean _Running = false;

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
    }

    private final class WriterThread extends MemoThread {

        public void run() {
            try {
                while (_Running) {
                    writerLoop();
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                stopThread();
                Timber.w(e, "WriterThread thread quit.");
            }
        }
    }

    private final class DumperThread extends MemoThread {

        @Override
        public void run() {
            String ffn = "";
            try {
                Process Process = Runtime.getRuntime().exec(cliString);
                InputStream input = Process.getInputStream();
                BufferedReader mReader = new BufferedReader(new InputStreamReader(input), 4096);

                ffn = generateFullBookName(storeDirectory, tip.Level.name());
                File file = Tools.makeFile(ffn);

                SimpleDateFormat tf = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);

                FileOutputStream os = new FileOutputStream(file);
                OutputStreamWriter ow = new OutputStreamWriter(os);
                try (BufferedWriter writer = new BufferedWriter(ow)) {
                    String line;
                    while (_Running) {
                        line = mReader.readLine();
                        if (line == null) {
                            sleep(1000);
                        } else {
                            StringBuilder tb = new StringBuilder(
                                    tf.format(System.currentTimeMillis()));
                            writer.write(tb.append(" ").append(line).append("\n")
                                    .toString());
                        }
                    }
                } catch (InterruptedException e) {
                    Process.destroy();
                    stopThread();
                    Timber.w(e, "Dumper Thread thread quit.");
                }
            } catch (IOException e) {
                Timber.w(e, "I/O Stream Error: %s", ffn);
            }
        }
    }
}
