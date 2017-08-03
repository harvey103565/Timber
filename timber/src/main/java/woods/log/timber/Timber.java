package woods.log.timber;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This code is a modified copy from JakeWharton's timber project
 * You can find the original code at Github. Url: "https://github.com/JakeWharton/timber"
 * Changes:
 * - Remove class DebugLog
 * - Draw interface 'Tree' out of class Timber
 * - Move getStackTrace() method to class Tools
 * - Re-code 'TREE_OF_SOULS'
 */


public class Timber {

    /**
     * Log a verbose message with optional format args.
     */
    public static void v(@NonNull String message, Object... args) {
        asTree().v(message, args);
    }

    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().v(t, message, args);
    }

    /**
     * Log a debug message with optional format args.
     */
    public static void d(@NonNull String message, Object... args) {
        asTree().d(message, args);
    }

    /**
     * Log a debug exception and a message with optional format args.
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
     * Log an info exception and a message with optional format args.
     */
    public static void i(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().i(t, message, args);
    }

    /**
     * Log a warning message with optional format args.
     */
    public static void w(@NonNull String message, Object... args) {
        asTree().w(message, args);
    }

    /**
     * Log a warning exception and a message with optional format args.
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
     * Log an error exception and a message with optional format args.
     */
    public static void e(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().e(t, message, args);
    }

    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(@NonNull String message, Object... args) {
        asTree().wtf(message, args);
    }

    /**
     * Log an assert exception and a message with optional format args.
     */
    public static void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
        asTree().wtf(t, message, args);
    }

    /**
     * Set a one-time tag for use on the next logging call.
     */
    public static Tree tag(String tag) {
        asTree().tag(tag);
        return asTree();
    }

    /**
     * Add a new logging tree.
     */
    public static Tree policy(Policy policy) {
        asTree().policy(policy);
        return asTree();
    }

    /**
     * Add a new logging tree.
     */
    public static Tree prober(Prober prober) {
        asTree().prober(prober);
        return asTree();
    }

    /**
     * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
     * instance rather than using static methods or to facilitate testing.
     */
    public static Tree asTree() {
        return TREE_OF_SOULS;
    }


    public static WoodsBuilder builder() {
        return new WoodsBuilder();
    }


    /**
     * Adds new logging trees.
     */
    public static void plant(@NonNull Tree... trees) {
        for (Tree tree : trees) {
            if (tree == null) {
                throw new NullPointerException("trees contains null");
            }
            if (tree == TREE_OF_SOULS) {
                throw new IllegalArgumentException("Cannot plant Timber into itself.");
            }
        }
        synchronized (FOREST) {
            Collections.addAll(FOREST, trees);
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
        for (Tree tree : trees) {
            tree.plant();
        }
    }

    /**
     * Remove a planted tree.
     */
    public static void uproot(@NonNull Tree tree) {
        synchronized (FOREST) {
            if (!FOREST.remove(tree)) {
                throw new IllegalArgumentException("Cannot uproot tree which is not planted: " + tree);
            }
            forestAsArray = FOREST.toArray(new Tree[FOREST.size()]);
        }
        tree.uproot();
    }

    /**
     * Remove all planted trees.
     */
    public static void uprootAll() {
        Tree[] trees = forestAsArray;

        synchronized (FOREST) {
            FOREST.clear();
            forestAsArray = TREE_ARRAY_EMPTY;
        }

        for (Tree tree : trees) {
            tree.uproot();
        }
    }


    private static final Tree[] TREE_ARRAY_EMPTY = new Tree[0];
    private static final List<Tree> FOREST = new ArrayList<>();
    private static volatile Tree[] forestAsArray = TREE_ARRAY_EMPTY;

    /**
     * A {@link Tree} that delegates to all planted trees in the {@linkplain #FOREST forest}.
     */
    private static final Tree TREE_OF_SOULS = new Tree() {

        private Prober _Prober = null;

        /** Set a one-time tag for use on the next logging call. */
        @Override
        public Tree tag(String tag) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.tag(tag);
            }
            return TREE_OF_SOULS;
        }

        @Override
        public Tree policy(Policy policy) {
            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.policy(policy);
            }
            return TREE_OF_SOULS;
        }

        @Override
        public Tree prober(Prober prober) {
            _Prober = prober;

            Tree[] forest = forestAsArray;
            for (Tree tree : forest) {
                tree.prober(prober);
            }
            return TREE_OF_SOULS;
        }

        @Override
        public void v(@NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(message, args);
            }
        }

        @Override
        public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(t, message, args);
            }
        }

        @Override
        public void d(@NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(message, args);
            }
        }

        @Override
        public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(t, message, args);
            }
        }

        @Override
        public void i(@NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(message, args);
            }
        }

        @Override
        public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(t, message, args);
            }
        }

        @Override
        public void w(@NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(message, args);
            }
        }

        @Override
        public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(t, message, args);
            }
        }

        @Override
        public void e(@NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(message, args);
            }
        }

        @Override
        public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(t, message, args);
            }
        }

        @Override
        public void wtf(@NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(message, args);
            }
        }

        @Override
        public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (_Prober != null) {
                _Prober.probe();
            }

            Tree[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(t, message, args);
            }
        }

        @Override
        public void plant() {
            throw new AssertionError("Howto plant 'Tree of soul'?");
        }

        @Override
        public void uproot() {
            throw new AssertionError("Howto uproot a 'Tree of soul'?");
        }
    };

    private Timber() {
        throw new AssertionError("No instances for woods.");
    }
}
