package woods.log.timber;



/**
 * A onplant: able to be added to forest
 */

interface Plant {
    /**
     * Called when tree is added into forest.
     */
    void onplant(Probe probe);

    /**
     * Called when tree is removed from forest.
     */
    void onuproot();


    void pin(String notes);
}
