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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * A tree to dump all logs of current process into one file('memo'), for persisting purpose.
 * Use {@link Pin} annotation to configure for simple.
 * It can writes each message into single file('Paper') as well. Use {@link Pin} annotation
 * for simple to configure.
 */



public class MemoTree extends Wood {

    private static final String ACCURATETIME = "MM-dd HH-mm-ss.SSS";

    private static final String BRIEFTIME = "MM-dd HH-mm";

    private volatile String storeDirectory = null;

    private String cliString = null;


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

    private final class DumperThread extends MemoThread {

        @Override
        public void run() {
            String ffn = "";
            try {
                Process Process = Runtime.getRuntime().exec(cliString);
                InputStream input = Process.getInputStream();
                BufferedReader mReader = new BufferedReader(new InputStreamReader(input), 4096);

                ffn = generateFullBookName(storeDirectory, Level.E.name());
                File file = Tools.makeFile(ffn);

                SimpleDateFormat tf = new SimpleDateFormat(ACCURATETIME, Locale.CHINA);

                FileOutputStream os = new FileOutputStream(file);
                OutputStreamWriter ow = new OutputStreamWriter(os);
                BufferedWriter writer = new BufferedWriter(ow);
                try  {
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
                } finally {
                    os.flush();
                    os.close();
                    writer.close();
                }
            } catch (IOException e) {
                Timber.w(e, "I/O Stream Error: %s", ffn);
            }
        }
    }
}
