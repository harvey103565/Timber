package woods.log.sample;

import woods.log.timber.Pin;
import woods.log.timber.Wood;

/**
 * Interface to generate trees used in woods
 */
public interface Seed {
    @Pin("{\"Class\":\"SampleActivity\", \"Level\":\"W\", \"Thread\":\"main\", \"Filters\":[\"I\",\"E\",\"A\"]}")
    Wood debugTree();
}
