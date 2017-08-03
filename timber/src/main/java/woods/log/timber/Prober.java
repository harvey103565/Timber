package woods.log.timber;

/**
 * Interface to get the running Environment
 */

public interface Prober {
    /**
     * Probe the running context parameter
     */
    void probe();

    /**
     * Return the build type of host package
     */
    String getStoragePath();

    /**
     * Return the name of Calling class
     */
    String getClassName();

    /**
     * Return the name of package which calling class resides
     */
    String getPackageName();

    /**
     * Return the name of thread that calling functions locates
     */
    String getThreadName();

    /**
     * Return the name of method where call happens
     */
    String getMethodName();

    /**
     * Return the file name and line# where call is.
     */
    String getFileLine();
}
