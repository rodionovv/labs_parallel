import org.apache.zookeeper.*;

import java.io.IOException;

public class Zoo {

    private static final String ZOO_KEEPER_HOST = "127.0.0.1:2181";
    private static final String ZOO_KEEPER_SERVER_DIR = "/servers";
    private static final String ZOO_KEEPER_CHILD_DIR = "/servers/";
    private final static int TIMEOUT = 5000;

    private String port;
    private ZooKeeper zoo;
    private UpdateWatcher watcher;

    Zoo(int port) throws IOException {
        this.port = Integer.toString(port);
        this.zoo = new ZooKeeper(
                ZOO_KEEPER_HOST,
                TIMEOUT,
                new UpdateWatcher()
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
                new UpdateWatcher()
        );
    }


    public static class UpdateWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {

        }
    }


}
