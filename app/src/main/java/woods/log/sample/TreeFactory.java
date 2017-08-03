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
    @Paper("{\"Thread\":\"http-thread\",\"Class\":\"LoginActivity\"," +
            "\"Filters\":[\"V\",\"I\",\"W\",\"E\",\"A\"]}")
    @Echo("{\"Level\":\"W\"}")
    Tree createTree();

    @Custom("{\"Package\":\"android.log.utils.sample\"}")
    CatcherTree createCatcherTree();
}
