package woods.log.sample;

import woods.log.timber.EchoTree;
import woods.log.timber.MemoTree;
import woods.log.timber.Pin;

/**
 * Interface to generate trees used in woods
 */
public interface TreeFactory {
/*
 *
 *
*/
@Pin("{\"Class\":\"SampleActivity\", \"Level\":\"V\"}")
    EchoTree createDebugTree();

    @Pin("{\"Level\":\"W\", \"Thread\":\"main\", \"Filters\":[\"I\",\"E\",\"A\"]}")
    MemoTree createMemoTree();
}
