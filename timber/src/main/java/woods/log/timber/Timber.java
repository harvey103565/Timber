package woods.log.timber;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


/**
 * This code is a modified copy from JakeWharton's timber project
 * You can find the original code at Github. Url: "https://github.com/JakeWharton/timber"
 * Changes:
 * - Remove class DebugLog
 * - Draw interface 'Plant' out of class Timber
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
    public static Wood tag(String tag) {
        return asTree().tag(tag);
    }

    /**
     * A view into Timber's planted trees as a tree itself. This can be used for injecting a logger
     * instance rather than using static methods or to facilitate testing.
     */
    public static Wood asTree() {
        return TREE_OF_SOULS;
    }


    /**
     *
     */
    public static WoodsBuilder builder() {
        return new WoodsBuilder();
    }


    /**
     * Adds new logging trees.
     */
    public static void plant(@NonNull Plant plant) {
        if (plant == TREE_OF_SOULS) {
            throw new AssertionError("Cannot plant 'TREE_OF_SOULS'.");
        }

        Plants.add(plant);

        if (plant instanceof Wood) {
            Forest.add((Wood)plant);
        }

        synchronized (Forest) {
            forestAsArray = Forest.toArray(new Wood[Forest.size()]);
        }
    }

    /**
     * Remove a planted onplant.
     */
    public static void uproot(@NonNull Plant plant) {

        synchronized (Plants) {
            if (!Plants.remove(plant)) {
                throw new AssertionError("Cannot uproot plant which is not planted: " + plant);
            }
        }
        if (plant instanceof Wood) {
            synchronized (Forest) {
                if (!Forest.remove(plant)) {
                    throw new AssertionError("Cannot uproot plant which is not planted: " + plant);
                }
                forestAsArray = Forest.toArray(new Wood[Forest.size()]);
            }
        }
        plant.onuproot();
    }

    /**
     * Remove all planted trees.
     */
    public static void uprootAll() {
        Plant[] plants = forestAsArray;

        synchronized (Plants) {
            Plants.clear();
        }

        synchronized (Forest) {
            Forest.clear();
            forestAsArray = TREE_ARRAY_EMPTY;
        }

        for (Plant plant : plants) {
            plant.onuproot();
        }
    }


    private static final Wood[] TREE_ARRAY_EMPTY = new Wood[0];

    private static final List<Plant> Plants = new ArrayList<>();

    private static final List<Wood> Forest = new ArrayList<>();

    private static volatile Wood[] forestAsArray = TREE_ARRAY_EMPTY;

    /**
     * A {@link Wood} that delegates to all planted trees in the {@linkplain #Forest forest}.
     */
    private static final Wood TREE_OF_SOULS = new Wood() {

        private Probe EnvProbe = null;

        /** Set a one-time tag for use on the next logging call. */
        public Wood tag(String tag) {
            if (EnvProbe != null) {
                EnvProbe.setCustomTag(tag);
            }
            return TREE_OF_SOULS;
        }

        @Override
        public void v(@NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;

            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(message, args);
            }
        }

        @Override
        public void v(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].v(t, message, args);
            }
        }

        @Override
        public void d(@NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(message, args);
            }
        }

        @Override
        public void d(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].d(t, message, args);
            }
        }

        @Override
        public void i(@NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(message, args);
            }
        }

        @Override
        public void i(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].i(t, message, args);
            }
        }

        @Override
        public void w(@NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(message, args);
            }
        }

        @Override
        public void w(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].w(t, message, args);
            }
        }

        @Override
        public void e(@NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(message, args);
            }
        }

        @Override
        public void e(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].e(t, message, args);
            }
        }

        @Override
        public void wtf(@NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(message, args);
            }
        }

        @Override
        public void wtf(@NonNull Throwable t, @NonNull String message, Object... args) {
            if (!envprobe()) {
                return;
            }

            Wood[] forest = forestAsArray;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0, count = forest.length; i < count; i++) {
                forest[i].wtf(t, message, args);
            }
        }

        @Override
        public void onplant(Probe probe) {
            EnvProbe = probe;
        }

        @Override
        public void onuproot() {
            throw new AssertionError("Uproot a 'Tree of soul'?");
        }

        @Override
        public void pin(String notes) {

        }

        private boolean envprobe() {
            if (EnvProbe == null) {
                return false;
            }

            EnvProbe.probe();
            return true;
        }
    };

    private Timber() {
        throw new AssertionError("No instances for 'Timber'.");
    }
}
