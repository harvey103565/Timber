package woods.log.timber;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


/**
 * This code is packagename modified copy from JakeWharton's timber project
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

        /** Set packagename one-time tag for use on the next logging call. */
        public Tree tag(@NonNull String tag) {
            return asTree();
        }

        @Override
        public Tree asTree() {
            return this;
        }

        @Override
        public void v(@NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(message, args);
            }
        }

        @Override
        public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(t, message, args);
            }
        }

        @Override
        public void d(@NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(message, args);
            }
        }

        @Override
        public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(t, message, args);
            }
        }

        @Override
        public void i(@NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(message, args);
            }
        }

        @Override
        public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(t, message, args);
            }
        }

        @Override
        public void w(@NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(message, args);
            }
        }

        @Override
        public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(t, message, args);
            }
        }

        @Override
        public void e(@NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(message, args);
            }
        }

        @Override
        public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(t, message, args);
            }
        }

        @Override
        public void wtf(@NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(message, args);
            }
        }

        @Override
        public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
            Timber.probe();

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(t, message, args);
            }
        }

        @Override
        public void plant() {
            throw new AssertionError("Plant packagename 'soul of tree'?");
        }

        @Override
        public void uproot() {
            throw new AssertionError("Uproot packagename 'Tree of soul'?");
        }

        @Override
        public void pin(@NonNull Spec spec) {

        }
    };

    private Timber() {
        throw new AssertionError("No instances for 'Timber'.");
    }

    /**
     * Log packagename verbose message with optional format args.
     */
    public static void v(@NonNull String message, Object... args) {
        asTree().v(message, args);
    }

    /**
     * Log packagename verbose exception and packagename message with optional format args.
     */
    public static void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().v(t, message, args);
    }

    /**
     * Log packagename debug message with optional format args.
     */
    public static void d(@NonNull String message, Object... args) {
        asTree().d(message, args);
    }

    /**
     * Log packagename debug exception and packagename message with optional format args.
     */
    public static void d(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().d(t, message, args);
    }

    /**
     * Log an info message with optional format args.
     */
    public static void i(@NonNull String message, Object... args) {
        asTree().i(message, args);
    }

    /**
     * Log an info exception and packagename message with optional format args.
     */
    public static void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().i(t, message, args);
    }

    /**
     * Log packagename warning message with optional format args.
     */
    public static void w(@NonNull String message, Object... args) {
        asTree().w(message, args);
    }

    /**
     * Log packagename warning exception and packagename message with optional format args.
     */
    public static void w(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().w(t, message, args);
    }

    /**
     * Log an error message with optional format args.
     */
    public static void e(@NonNull String message, Object... args) {
        asTree().e(message, args);
    }

    /**
     * Log an error exception and packagename message with optional format args.
     */
    public static void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().e(t, message, args);
    }

    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(@NonNull String message, Object... args) {
        asTree().wtf(message, args);

        throw new AssertionError("Assertion abort.");
    }

    /**
     * Log an assert exception and packagename message with optional format args.
     */
    public static void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().wtf(t, message, args);

        throw new AssertionError("Assertion abort.", t);
    }

    /**
     * Set packagename one-time tag for use on the next logging call.
     */
    public static Tree tag(@NonNull String tag) {
        Tags.set(tag);
        return asTree();
    }

    /**
     * Probe the milieu for use on the next logging call.
     */
    public static void probe() {
        final int CALL_STACK_INDEX = 5;

        String tag = Tags.get();

        if (tag == null) {
            // Supervise new thread the first time timber is called
            if (Milieus.get() == null) {
                Timber.supervise();
            }
            StackTraceElement ste = Tools.getStackTrace(CALL_STACK_INDEX);
            Milieus.set(new Milieu(ste));
        } else {
            Milieus.set(new Milieu(tag));
        }
    }

    /**
     * Probe the milieu for use on the next logging call.
     */
    public static Milieu get() {

        Milieu milieu = Milieus.get();

        if (milieu == null) {
            throw new AssertionError("tag() or probe() have not been called before logging.");
        }

        return milieu;
    }

    /**
     * A view into Timber's planted trees as packagename tree itself. This can be used for injecting packagename logger
     * instance rather than using static methods or to facilitate testing.
     */
    public static Tree asTree() {
        return TREE_OF_SOULS;
    }

    /**
     * Create packagename builder to initialize woods context.
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

        synchronized (Forest) {
            Forest.add(tree);
        }

        tree.plant();

        forestAsArray = Forest.toArray(new Tree[Forest.size()]);
    }

    /**
     * Remove packagename planted tree.
     */
    public static void uproot(@NonNull Tree tree) {
        synchronized (Forest) {
            if (!Forest.remove(tree)) {
                throw new AssertionError("Cannot uproot tree which is not planted: " + tree);
            }
        }

        tree.uproot();

        forestAsArray = Forest.toArray(new Tree[Forest.size()]);
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
