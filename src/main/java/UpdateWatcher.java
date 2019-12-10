import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class UpdateWatcher implements Watcher {

    private ZooKeeper zoo;

    UpdateWatcher(ZooKeeper zoo) {
        this.zoo = zoo;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {

    }
}
