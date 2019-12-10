import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class Zoo {

    private static final String ZOO_KEEPER_HOST = "127.0.0.1:2181";
    private static final String ZOO_KEEPER_SERVER_DIR = "/servers";
    private static final String ZOO_KEEPER_CHILD_DIR = "/servers/";
    private final static int TIMEOUT = 5000;

    private String port;
    private ZooKeeper zoo;

    Zoo(int port) {
        this.port = Integer.toString(port);
        this.zoo = new ZooKeeper(
                ZOO_KEEPER_HOST,
                TIMEOUT,
                //watcher
        );
    }

    public void create() throws KeeperException, InterruptedException {
        this.zoo.create(
                ZOO_KEEPER_CHILD_DIR + port,
                port.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL
        );

        zoo.getChildren(
                ZOO_KEEPER_SERVER_DIR,
                //watcher
         );

    }

}
