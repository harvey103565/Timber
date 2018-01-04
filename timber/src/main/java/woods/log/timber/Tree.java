package woods.log.timber;

import android.support.annotation.NonNull;


public interface Tree {

    /**
     * Called when tree is added into forest.
     */
    void plant();

    /**
     * Called when tree is removed from forest.
     */
    void uproot();

    /**
     * Apply notation to the tree
     */
    void pin(@NonNull Spec spec);

    /**
     * Log verbose message with optional format args.
     */
    void v(@NonNull String message, Object... args);

    /**
     * Log verbose exception and message with optional format args.
     */
    void v(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log debug message with optional format args.
     */
    void d(@NonNull String message, Object... args);

    /**
     * Log debug exception and message with optional format args.
     */
    void d(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an info message with optional format args.
     */
    void i(@NonNull String message, Object... args);

    /**
     * Log an info exception and message with optional format args.
     */
    void i(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log warning message with optional format args.
     */
    void w(@NonNull String message, Object... args);

    /**
     * Log warning exception and message with optional format args.
     */
    void w(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an error message with optional format args.
     */
    void e(@NonNull String message, Object... args);

    /**
     * Log an error exception and message with optional format args.
     */
    void e(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an assert message with optional format args.
     */
    void wtf(@NonNull String message, Object... args);

    /**
     * Log an assert exception and message with optional format args.
     */
    void wtf(@NonNull Throwable t, @NonNull String message, Object... args);
}
