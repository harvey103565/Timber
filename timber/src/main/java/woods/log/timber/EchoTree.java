package woods.log.timber;

import android.util.Log;



/**
 * A tree put messages into standard log console
 */

public class EchoTree extends Wood {

    private static final int MAX_LOG_LENGTH = 2048;

    /**
     * Break up {@code log} into maximum-length chunks (if needed) and send to either
     * {@link Log#println(int, String, String) Log.println()} or
     * {@link Log#wtf(String, String) Log.wtf()} for logging.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void log(int priority, String tag, String message, Throwable t) {
        StringBuilder ms = new StringBuilder(message);
        if (t != null) {
            String stacktrace = Tools.serializeException(t);
            ms.append("\n").append(stacktrace);
        }

        if (ms.length() < MAX_LOG_LENGTH) {
            if (priority == WTF) {
                Log.wtf(tag, ms.toString());
            } else {
                Log.println(priority, tag, ms.toString());
            }
        } else {
            logByLines(priority, tag, ms.toString());
        }
    }

    /**
     * Break up {@code message} into maximum-length chunks (if needed) and send to either
     * {@link Log#println(int, String, String) Log.println()} or
     * {@link Log#wtf(String, String) Log.wtf()} for logging.
     * <p>
     * {@inheritDoc}
     */
    public void logByLines(int priority, String tag, String message) {

        // Split by line, then ensure each line can fit into Log's maximum length.
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
/**
 *              We dont' want to exit program here
 *              if (priority == Log.ASSERT) {
 *                  Log.wtf(tag, part);
 *              } else {
 *                  Log.println(priority, tag, part);
 *              }
 *
 */
                Log.println(priority == WTF ? Log.ERROR : priority, tag, part);
                i = end;
            } while (i < newline);
        }
    }
}
