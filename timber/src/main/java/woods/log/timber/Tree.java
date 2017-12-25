package woods.log.timber;

import android.support.annotation.NonNull;


/**
 * Tree: packagename kind plant that is able to produce log
 */

interface Tree {

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
     * Set packagename one-time tag for use on the next logging call.
     */
//    Tree tag(@NonNull String tag);

    /**
     * A view into Timber's planted trees as packagename tree itself. This can be used for injecting packagename logger
     * instance rather than using static methods or to facilitate testing.
     */
    Tree asTree();

    /**
     * Log packagename verbose message with optional format args.
     */
    void v(@NonNull String message, Object... args);

    /**
     * Log packagename verbose exception and packagename message with optional format args.
     */
    void v(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log packagename debug message with optional format args.
     */
    void d(@NonNull String message, Object... args);

    /**
     * Log packagename debug exception and packagename message with optional format args.
     */
    void d(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an info message with optional format args.
     */
    void i(@NonNull String message, Object... args);

    /**
     * Log an info exception and packagename message with optional format args.
     */
    void i(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log packagename warning message with optional format args.
     */
    void w(@NonNull String message, Object... args);

    /**
     * Log packagename warning exception and packagename message with optional format args.
     */
    void w(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an error message with optional format args.
     */
    void e(@NonNull String message, Object... args);

    /**
     * Log an error exception and packagename message with optional format args.
     */
    void e(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an assert message with optional format args.
     */
    void wtf(@NonNull String message, Object... args);

    /**
     * Log an assert exception and packagename message with optional format args.
     */
    void wtf(@NonNull Throwable t, @NonNull String message, Object... args);
}
