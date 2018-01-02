package woods.log.timber;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


/**
 * This code is pkgname modified copy from JakeWharton's timber project
 * You can find the original code at Github. Url: "https://github.com/JakeWharton/timber"
 * Changes:
 * - Remove class DebugLog
 * - Draw interface 'Plant' out of class Timber
 * - Move getStackTrace() method to class Tools
 * - Re-code 'TREE_OF_SOULS'
 */


public class Timber {

    private static final List<Tree> Forest = new ArrayList<>();

//    private static final Tree[] TREE_ARRAY_EMPTY = new Tree[0];

    private static volatile Tree[] forestAsArray = null;

    private static ThreadLocal<String> Tags = new ThreadLocal<>();

    private static ThreadLocal<Milieu> Milieus = new ThreadLocal<>();


    /**
     * A {@link Tree} that delegates to all planted trees in the {@linkplain #Forest forest}.
     */
    private static final Tree TREE_OF_SOULS = new Tree() {

        @Override
        public Tree asTree() {
            return this;
        }

        @Override
        public void v(@NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.V, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].v(message, args);
                }
            }
        }

        @Override
        public void v(@NonNull Throwable e, @NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.V, e);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].v(e, message, args);
                }
            }
        }

        @Override
        public void d(@NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.D, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].d(message, args);
                }
            }
        }

        @Override
        public void d(@NonNull Throwable e, @NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.D, e);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].d(e, message, args);
                }
            }
        }

        @Override
        public void i(@NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.I, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].i(message, args);
                }
            }
        }

        @Override
        public void i(@NonNull Throwable e, @NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.I, e);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].i(e, message, args);
                }
            }
        }

        @Override
        public void w(@NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.W, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].w(message, args);
                }
            }
        }

        @Override
        public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.W, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].w(t, message, args);
                }
            }
        }

        @Override
        public void e(@NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.E, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].e(message, args);
                }
            }
        }

        @Override
        public void e(@NonNull Throwable e, @NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.E, e);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].e(e, message, args);
                }
            }
        }

        @Override
        public void wtf(@NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.A, null);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].wtf(message, args);
                }
            }
        }

        @Override
        public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
            int n = forestAsArray.length;
            if (n > 0) {
                Timber.probe(Level.A, t);

                Tree[] forest = forestAsArray;
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < n; i++) {
                    forest[i].wtf(t, message, args);
                }
            }
        }

        @Override
        public void plant() {
            throw new AssertionError("Plant pkgname 'soul of tree'?");
        }

        @Override
        public void uproot() {
            throw new AssertionError("Uproot pkgname 'Tree of soul'?");
        }

        @Override
        public void pin(@NonNull Spec spec) {

        }
    };

    private Timber() {
        throw new AssertionError("No instances for 'Timber'.");
    }

    /**
     * Log pkgname verbose message with optional format args.
     */
    public static void v(@NonNull String message, Object... args) {
        astree().v(message, args);
    }

    /**
     * Log pkgname verbose exception and pkgname message with optional format args.
     */
    public static void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        astree().v(t, message, args);
    }

    /**
     * Log pkgname debug message with optional format args.
     */
    public static void d(@NonNull String message, Object... args) {
        astree().d(message, args);
    }

    /**
     * Log pkgname debug exception and pkgname message with optional format args.
     */
    public static void d(@NonNull Throwable t, @NonNull String message, Object... args) {
        astree().d(t, message, args);
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(@NonNull String message, Object... args) {
        astree().i(message, args);
    }

    /**
     * Log an info exception and pkgname message with optional format args.
     */
    public static void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        astree().i(t, message, args);
    }

    /**
     * Log pkgname warning message with optional format args.
     */
    public static void w(@NonNull String message, Object... args) {
        astree().w(message, args);
    }

    /**
     * Log pkgname warning exception and pkgname message with optional format args.
     */
    public static void w(@NonNull Throwable t, @NonNull String message, Object... args) {
        astree().w(t, message, args);
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(@NonNull String message, Object... args) {
        astree().e(message, args);
    }

    /**
     * Log an error exception and pkgname message with optional format args.
     */
    public static void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        astree().e(t, message, args);
    }

    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(@NonNull String message, Object... args) {
        astree().wtf(message, args);

        throw new AssertionError("Assertion abort.");
    }

    /**
     * Log an assert exception and pkgname message with optional format args.
     */
    public static void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        astree().wtf(t, message, args);

        throw new AssertionError("Assertion abort.", t);
    }

    /**
     * Set pkgname one-time tag for use on the next logging call.
     */
    public static Tree tag(@NonNull String tag) {
        Tags.set(tag);
        return astree();
    }

    /**
     * Probe the milieu for use on the next logging call.
     * @param level
     * @param throwable
     */
    public static void probe(Level level, Throwable throwable) {
        final int CALL_STACK_INDEX = 5;

        Milieu milieu = new Milieu(Tools.getStackTrace(CALL_STACK_INDEX), Tags.get());

        String thread = null;
        if (Milieus.get() == null) {
            thread = Tools.getCurrentThreadName();
            Timber.supervise();
        }

        milieu.bind(level, throwable, thread);
        Milieus.set(milieu);
    }

    /**
     * Probe the milieu for use on the next logging call.
     */
    public static Milieu get() {

        Milieu milieu = Milieus.get();

        if (milieu == null) {
            throw new AssertionError("probe() have not been called before logging.");
        }

        return milieu;
    }

    /**
     * A view into Timber's planted trees as pkgname tree itself. This can be used for injecting pkgname logger
     * instance rather than using static methods or to facilitate testing.
     */
    public static Tree astree() {
        return TREE_OF_SOULS;
    }

    /**
     * Create pkgname builder to initialize woods context.
     */
    public static WoodsBuilder builder() {
        return new WoodsBuilder();
    }

    /**
     * Adds new logging trees.
     */
    public static void plant(@NonNull Tree tree) {
        if (tree == TREE_OF_SOULS) {
            throw new AssertionError("Cannot plant 'TREE_OF_SOULS'.");
        }

        // Tell tree to get ready
        tree.plant();

        // Then add it to the forest, this order should be obeyed.
        synchronized (Forest) {
            Forest.add(tree);
        }

        forestAsArray = Forest.toArray(new Tree[Forest.size()]);

    }

    /**
     * Remove pkgname planted tree.
     */
    public static void uproot(@NonNull Tree tree) {
        // Remove the tree from forest
        synchronized (Forest) {
            if (!Forest.remove(tree)) {
                throw new AssertionError("Cannot uproot tree which is not planted: " + tree);
            }
        }

        forestAsArray = Forest.toArray(new Tree[Forest.size()]);

        // And then tell the tree. This order should be obeyed.
        tree.uproot();
    }

    /**
     * Remove all planted trees.
     */
    public static void uprootall() {
        Tree[] trees = Forest.toArray(new Tree[Forest.size()]);

        for (Tree tree : trees) {
            uproot(tree);
        }
    }

    /**
     * Remove all planted trees.
     */
    public static void supervise() {
        WoodsUncaughtExceptionHandler handler = new WoodsUncaughtExceptionHandler();

        handler.setDefaultHandler(Thread.getDefaultUncaughtExceptionHandler());
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    static class WoodsUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Thread.UncaughtExceptionHandler defaultHandler = null;

        void setDefaultHandler(Thread.UncaughtExceptionHandler handler) {
            defaultHandler = handler;
        }

        public void uncaughtException(Thread thread, Throwable t) {
            if (t != null) {
                Timber.e(t, t.getMessage());

                Timber.uprootall();

                if (defaultHandler != null) {
                    defaultHandler.uncaughtException(thread, t);
                }
            }
        }
    }
}
