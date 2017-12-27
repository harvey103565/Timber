package woods.log.timber;

import android.util.Log;

/**
 *
 */

public enum Level {
    ALL(1),
    V(Log.VERBOSE),
    D(Log.DEBUG),
    I(Log.INFO),
    W(Log.WARN),
    E(Log.ERROR),
    A(Log.ASSERT),
    S(8);

    int Priority;

    Level(int priority) {
        Priority = priority;
    }

    int Priority() {
        return Priority;
    }
}
