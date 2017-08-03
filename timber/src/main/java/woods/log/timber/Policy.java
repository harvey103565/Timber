package woods.log.timber;

/**
 * A configuration for trees
 */
public class Policy {

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
     * Specified to package
     */
    String Package;

    /**
     * Specified to catalog
     */
    String Catalog;

    /**
     * Addtional options, for example a json string,
     */
    String Extension;
}
