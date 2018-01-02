package woods.log.timber;

import android.support.annotation.NonNull;


/**
 * Tree: pkgname kind plant that is able to produce log
 */

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
     * Set pkgname one-time tag for use on the next logging call.
     */
//    Tree tag(@NonNull String tag);

    /**
     * A view into Timber's planted trees as pkgname tree itself. This can be used for injecting pkgname logger
     * instance rather than using static methods or to facilitate testing.
     */
    Tree asTree();

    /**
     * Log pkgname verbose message with optional format args.
     */
    void v(@NonNull String message, Object... args);

    /**
     * Log pkgname verbose exception and pkgname message with optional format args.
     */
    void v(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log pkgname debug message with optional format args.
     */
    void d(@NonNull String message, Object... args);

    /**
     * Log pkgname debug exception and pkgname message with optional format args.
     */
    void d(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an info message with optional format args.
     */
    void i(@NonNull String message, Object... args);

    /**
     * Log an info exception and pkgname message with optional format args.
     */
    void i(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log pkgname warning message with optional format args.
     */
    void w(@NonNull String message, Object... args);

    /**
     * Log pkgname warning exception and pkgname message with optional format args.
     */
    void w(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an error message with optional format args.
     */
    void e(@NonNull String message, Object... args);

    /**
     * Log an error exception and pkgname message with optional format args.
     */
    void e(@NonNull Throwable t, @NonNull String message, Object... args);

    /**
     * Log an assert message with optional format args.
     */
    void wtf(@NonNull String message, Object... args);

    /**
     * Log an assert exception and pkgname message with optional format args.
     */
    void wtf(@NonNull Throwable t, @NonNull String message, Object... args);
}
