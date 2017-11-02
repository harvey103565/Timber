package woods.log.timber;

import android.support.annotation.NonNull;


/**
 * Wood: a kind onplant that is able to produce log
 */

interface Wood extends Plant {

    /**
     * Set tag for logs.
     */
    Wood tag(String tag);

    /**
     * Log a verbose message with optional format args.
     */
    void v(@NonNull String message, Object... args);

    /**
     * Log a verbose exception and a message with optional format args.
     */
    void v(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log a debug message with optional format args.
     */
    void d(@NonNull String message, Object... args);

    /**
     * Log a debug exception and a message with optional format args.
     */
    void d(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an info message with optional format args.
     */
    void i(@NonNull String message, Object... args);

    /**
     * Log an info exception and a message with optional format args.
     */
    void i(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log a warning message with optional format args.
     */
    void w(@NonNull String message, Object... args);

    /**
     * Log a warning exception and a message with optional format args.
     */
    void w(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an error message with optional format args.
     */
    void e(@NonNull String message, Object... args);

    /**
     * Log an error exception and a message with optional format args.
     */
    void e(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an assert message with optional format args.
     */
    void wtf(@NonNull String message, Object... args);

    /**
     * Log an assert exception and a message with optional format args.
     */
    void wtf(@NonNull Throwable t, @NonNull String message, Object... args);
}
