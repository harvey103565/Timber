package woods.log.timber;

/**
 * A configuration for trees
 */
public class Tip {

    /**
     * Switches that indicates log channel to open
     */
    Level[] Filters;

    /**
     * What levels of log is allowed
     */
    Level Level;

    /**
     * Specified to thread
     */
    String Thread;

    /**
     * Specified to class
     */
    String Class;

    /**
     * Specified to catalog
     */
    String Catalog;
}
