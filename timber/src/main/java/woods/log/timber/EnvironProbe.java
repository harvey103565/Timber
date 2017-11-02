package woods.log.timber;

import android.os.Environment;

import java.io.File;

/**
 *
 */

public class EnvironProbe implements Probe {

    private static final int CALL_STACK_INDEX = 5;

    private final String PackageName;

    private final ThreadLocal<StackTraceElement> ThreadTraces;

    private final ThreadLocal<String> ThreadTag;


    public EnvironProbe(String packageName) {
        PackageName = packageName;
        ThreadTraces = new ThreadLocal<>();
        ThreadTag = new ThreadLocal<>();
    }

    @Override
    public void probe() {
        StackTraceElement TraceElement = Tools.getStackTrace(CALL_STACK_INDEX);
        // TODO: OnError process when stacktrace not available
        ThreadTraces.set(TraceElement);
    }

    @Override
    public void setCustomTag(String tag) {
        ThreadTag.remove();
        ThreadTag.set(tag);
    }

    @Override
    public String getTag() {
        String customTag = ThreadTag.get();

        if (customTag != null) {
            ThreadTag.remove();
        } else {
            customTag = getClassName();
        }

        return customTag;
    }

    @Override
    public String getStoragePath() {
        StringBuilder pathbuilder = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            pathbuilder.append(Environment.getExternalStorageDirectory().getPath())
                    .append(File.separator).append("Android")
                    .append(File.separator).append("data");
        } else {
            pathbuilder.append(Environment.getDataDirectory().getPath())
                    .append(File.separator).append("data");
        }

        return pathbuilder.append(File.separator).append(PackageName).toString();
    }

    @Override
    public String getClassName() {
        StackTraceElement TraceElement = ThreadTraces.get();

        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        return Tools.getClassNameFromStack(TraceElement);
    }

    @Override
    public String getPackageName() {
        StackTraceElement TraceElement = ThreadTraces.get();

        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        return Tools.getPackageNameFromStack(TraceElement);
    }

    @Override
    public String getThreadName() {
        return Thread.currentThread().getName();
    }

    @Override
    public String getMethodName() {
        StackTraceElement TraceElement = ThreadTraces.get();

        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        return TraceElement.getMethodName();
    }

    @Override
    public String getFileLine() {
        StackTraceElement TraceElement = ThreadTraces.get();

        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        StringBuilder builder = new StringBuilder(TraceElement.getFileName());

        return builder.append(':').append(TraceElement.getLineNumber()).toString();
    }
}
