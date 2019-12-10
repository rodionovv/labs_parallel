import akka.actor.Actor;
import akka.actor.ActorRef;
import org.apache.zookeeper.*;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;

public class Zoo {

    private static final String ZOO_KEEPER_HOST = "127.0.0.1:2181";
    private static final String ZOO_KEEPER_SERVER_DIR = "/servers";
    private static final String ZOO_KEEPER_CHILD_DIR = "/servers/";
    private final static int TIMEOUT = 5000;

    private String port;
    private ZooKeeper zoo;
    private ActorRef storageActor;

    Zoo(int port, ActorRef storageActor) throws IOException {
        this.port = Integer.toString(port);
        this.zoo = new ZooKeeper(
                ZOO_KEEPER_HOST,
                TIMEOUT,
                new UpdateWatcher()
        );
        this.storageActor = storageActor;
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


    private class UpdateWatcher implements Watcher {

        @Override
        public void process(WatchedEvent watchedEvent) {
            List<String> servers = new ArrayList<>();
            try {
                servers = zoo.getChildren(ZOO_KEEPER_SERVER_DIR, this);
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

            List<String> serversData = new ArrayList<>();
            for (String s : servers) {
                try {
                    servers.add(new String(zoo.getData(ZOO_KEEPER_CHILD_DIR + s, false, null)));
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            storageActor.tell(
                    //msg(serversData),
                    ActorRef.noSender()
            );
        }
    }


}
