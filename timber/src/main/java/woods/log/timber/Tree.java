package woods.log.timber;

import android.support.annotation.NonNull;


/**
 * Tree: a kind plant that is able to produce log
 */

interface Tree {

    /**
     * Called when tree is added into forest.
     *
     * @param tree
     */
    void plant(@NonNull Tree tree);

    /**
     * Called when tree is removed from forest.
     *
     * @param tree
     */
    void uproot(@NonNull Tree tree);

    /**
     * Apply notation to the tree
     */
    void pin(@NonNull Tips tips);

    /**
     * Set a one-time tag for use on the next logging call.
     */
    Tree tag(@NonNull String tag);

    /**
     * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
     * instance rather than using static methods or to facilitate testing.
     */
    Tree asTree();

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
