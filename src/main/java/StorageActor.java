import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import java.util.List;
import java.util.Random;

public class StorageActor extends AbstractActor {

    List<String> serversData;
    int len;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(
                PortMessage.class,
                msg -> {
                    int randomPort;
                    Random rand = new Random()
                    while(serversData.get(new Random().nextInt(len)).equals(msg.getPort())) {
                        randomPort =
                    }
                }
        ).match(
                ServerMessage.class,
                msg -> {
                    serversData = msg.getServersData();
                    len = serversData.size();
                }
        ).build();
    }
}
