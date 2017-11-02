package woods.log.sample;

import woods.log.timber.CatcherTree;
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
    @Pin("{\"Class\":\"SampleActivity\", \"Filters\":[\"V\",\"I\",\"W\",\"E\",\"A\"]}")
    EchoTree createDebugTree();

    @Pin("{\"Level\":\"W\", \"Thread\":\"main\"}")
    MemoTree createMemoTree();

    @Pin("{\"Package\":\"woods.log.sample\"}")
    CatcherTree createCatcherTree();
}
