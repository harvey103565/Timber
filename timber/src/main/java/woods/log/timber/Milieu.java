package woods.log.timber;

import android.support.annotation.NonNull;

/**
 * Created by Harvey bv on 2017/11/28.
 */

public class Milieu {

    public String callerClassName;

    public String callerMethodName;

    public String threadName;

    public String packageName;

    public String fileLine;

    public String Tag;

    public Milieu(@NonNull StackTraceElement TraceElement) {
        callerClassName = Tools.getClassNameFromStack(TraceElement);
        Tag = callerClassName;

        packageName = Tools.getPackageNameFromStack(TraceElement);

        threadName = Thread.currentThread().getName();

        callerMethodName = TraceElement.getMethodName();

        StringBuilder builder = new StringBuilder(TraceElement.getFileName());
        fileLine = builder.append(':').append(TraceElement.getLineNumber()).toString();
    }

    public Milieu(@NonNull String tag) {
        Tag = tag;
    }
}
