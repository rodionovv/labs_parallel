import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import java.util.List;
public class StorageActor extends AbstractActor {

    List<String> serversData;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(
                //portClass,
                msg -> {

                }
        ).match(
                ServerMessage.class,
                msg -> {
                    serversData = msg.getServersData();
                }
        ).build();
    }
}
