package woods.log.sample;

import woods.log.timber.CatcherTree;
import woods.log.timber.Custom;
import woods.log.timber.Echo;
import woods.log.timber.Paper;
import woods.log.timber.Tree;

/**
 * Interface to generate trees used in woods
 */

public interface TreeFactory {
    @Paper("{\"Thread\":\"http-thread\",\"Package\":\"android.log.utils.sample\",\"Class\":" +
            "\"LoginActivity\",\"Level\":\"W\",\"Filters\":[\"V\",\"I\",\"W\",\"E\",\"A\"]}")
    @Echo("{\"Thread\":\"main\",\"Package\":\"android.log.utils.sample\",\"Level\":\"W\"}")
    Tree createTree();

    @Custom("{\"Thread\":\"http-thread\",\"Package\":\"android.log.utils.sample\"}")
    CatcherTree createCatcherTree();
}
