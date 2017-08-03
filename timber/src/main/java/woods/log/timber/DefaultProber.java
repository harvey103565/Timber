package woods.log.timber;

import android.os.Environment;

import java.io.File;

/**
 *
 */

public class DefaultProber implements Prober {

    private static final int CALL_STACK_INDEX = 5;

    private final String PackageName;

    private StackTraceElement TraceElement = null;

    public DefaultProber(String packageName) {
        PackageName = packageName;
    }

    @Override
    public void probe() {
        TraceElement = Tools.getStackTrace(CALL_STACK_INDEX);
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
        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        return Tools.getClassNameFromStack(TraceElement);
    }

    @Override
    public String getPackageName() {
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
        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        return TraceElement.getMethodName();
    }

    @Override
    public String getFileLine() {
        if (TraceElement == null) {
            throw new AssertionError("Probe() not invoked, forget? ", null);
        }

        StringBuilder builder = new StringBuilder(TraceElement.getFileName());

        return builder.append(TraceElement.getLineNumber()).toString();
    }
}
