package woods.log.timber;


import android.support.annotation.NonNull;


/**
 * This code is a modified copy from JakeWharton's timber project
 * You can find the original code at Github. Url: "https://github.com/JakeWharton/timber"
 * Changes:
 * - Add policy(), prober(), plant(), uproot() methods to interface 'Tree'
 */


public interface Tree {

    /**
     * Set tag for logs.
     */
    Tree tag(String tag);

    /**
     * Set logging policy for a tree.
     */
    Tree policy(Policy policy);

    /**
     * Set set prober to a tree.
     */
    Tree prober(Prober prober);

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

    /**
     * Called when tree is added into forest.
     */
    void plant();

    /**
     * Called when tree is removed from forest.
     */
    void uproot();
}
