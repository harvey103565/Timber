package woods.log.timber;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

    // Milliseconds in one hour
    public static final long ONE_HOUR_MILLIS = 60 * 60 * 1000;
    // Default maximum hours a file could be kept when reusing directory
    public static final int MAX_HOURS_TO_KEEP = 8;

    private static final int MAX_TAG_LENGTH = 30;

    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");


    /**
     * Get host process id.
     *
     * @return process id in integer
     */
    static public int getHostProcessId() {
        return android.os.Process.myPid();
    }

    /**
     * Get host thread id.
     *
     * @return thread id in integer
     */
    static public int getCurrentThreadId() {
        return android.os.Process.myTid();
    }

    /**
     * Get host thread name.
     *
     * @return thread name
     */
    static public String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * kill host process.
     */
    static public void killCurrentProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * force exit.
     */
    static public void forceExit() {
        System.exit(1);
    }

    /**
     * Collect Package's information for debugging purpose. Especially when there's a crash.
     *
     * @param c {@link Context} from app
     * @return Map object contains package information in key-pair format
     */
    static public Map<String, String> collectPackageInfo(@NonNull Context c) {

        // Map to gather package infomation
        Map<String, String> infos = new HashMap<>();

        try {
            PackageManager pm = c.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(c.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            infos.put(c.getPackageName(), "NameNotFound");
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            try {
                infos.put(name, field.get(null).toString());
            } catch (Exception e) {
                infos.put(name, "info not available");
            }
        }

        return infos;
    }

    /**
     * Convert key-values in map object into an string
     *
     * @param map map object to convert
     * @return Converted string
     */
    static public String serializeMap(@NonNull Map<?, ?> map) {

        // Convert Map object to string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            sb.append(key).append("=").append(value).append("\n\r");
        }

        return sb.toString();
    }

    /**
     * Print Exception's stacktrace to string
     *
     * @param t Throwable to convert
     * @return Stack trace string
     */
    static public String serializeException(@NonNull Throwable t) {
        StringBuilder sb = new StringBuilder();

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        t.printStackTrace(printWriter);

        Throwable cause = t.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = writer.toString();
        sb.append(result);
        printWriter.close();

        return sb.toString();
    }

    /**
     * Create a directory to store the log files
     *
     * @param name the directory to save logs
     * @return true if directory create successfully
     */
    static public File makeFile(@NonNull String name) throws IOException {
        File file = new File(name);

        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException(String.format("Error making file: %s", name));
            }
        } else {
            throw new IOException(String.format("%s already exist.", name));
        }

        return file;
    }

    /**
     * Create a directory to store the log files
     *
     * @param path the directory to save logs
     * @return true if directory create successfully
     */
    static public boolean makeDirectory(@NonNull String path) throws IOException {
        File directory = new File(path);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Error making directory.");
            }
        } else if (directory.isDirectory()) {
            flatDirectory(path, MAX_HOURS_TO_KEEP);
            Timber.w("Using existing log directory: %s.", path);
        } else {
            throw new IOException("Directory already exist.");
        }

        return true;
    }

    /**
     * Clear file in specified directory (to flat the land for new forest)
     *
     * @param path the directory to clear
     */
    static public void flatDirectory(@NonNull String path, int hours) {
        File logDir = new File(path);
        long nm = System.currentTimeMillis();

        try {
            if (logDir.exists()) {
                for (File file : logDir.listFiles()) {
                    if (!file.isDirectory()) {
                        long mm = file.lastModified();
                        if ((nm - mm) > (hours * ONE_HOUR_MILLIS)) {
                            if (!file.delete()) {
                                Timber.w("File not delete: %s/%s", file.getPath(), file.getName());
                            }
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            Timber.e(e, "Do not have privilege to access %s", path);
        }
    }

    /**
     * Extract the tag which should be used for the message from the {@code element}. By default
     * this will use the class name without any anonymous class suffixes (e.g., {@code Foo$1}
     * becomes {@code Foo}).
     * <p>
     */
    public static String getClassNameFromStack(StackTraceElement element) {
        String cls = element.getClassName();
        Matcher m = ANONYMOUS_CLASS.matcher(cls);
        if (m.find()) {
            cls = m.replaceAll("");
        }
        cls = cls.substring(cls.lastIndexOf('.') + 1);
        return cls.length() > MAX_TAG_LENGTH ? cls.substring(0, MAX_TAG_LENGTH) : cls;
    }

    /**
     * Extract the tag which should be used for the message from the {@code element}. By default
     * this will use the class name without any anonymous class suffixes (e.g., {@code Foo$1}
     * becomes {@code Foo}).
     * <p>
     */
    public static String getPackageNameFromStack(StackTraceElement element) {
        String fullname = element.getClassName();
        String file = element.getFileName();
        String cls = file.substring(0, file.indexOf('.'));

        String pkname = String.format("\\b([a-z][a-z0-9_]*(?:\\.[a-z0-9_]+)+)(?=\\.%s.*)\\b",
                cls);
        Matcher m = Pattern.compile(pkname).matcher(fullname);
        if (m.find()) {
            return m.group(0);
        }

        return null;
    }

    public static StackTraceElement getStackTrace(int pos) {

        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= pos) {
            throw new AssertionError
                    ("Synthetic stacktrace didn'Plant have enough elements: are you using proguard?");
        }
        return stackTrace[pos];
    }

    public static Tips parseTipString(String json) {
        Moshi moshi = new Moshi.Builder()
                .build();
        JsonAdapter<Tips> jsonAdapter = moshi.adapter(Tips.class);

        try {
            return jsonAdapter.fromJson(json);
        } catch (IOException e) {
            Timber.e(e, "Unknown policy: %s.", json);
        }
        return null;
    }
}

