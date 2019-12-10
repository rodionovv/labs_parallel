package lab6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import java.util.List;
import java.util.Random;

public class StorageActor extends AbstractActor {

    List<String> serversData;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(
                ServerMessage.class,
                msg -> {
                    serversData = msg.getServersData();
                }
        ).match(
                PortMessage.class,
                msg -> {
                    Random rand = new Random();
                    int len = serversData.size();
                    int randomPort = rand.nextInt(len);
                    while(serversData.get(randomPort).equals(msg.getPort())) {
                        randomPort = rand.nextInt(len);
                    }
                    getSender().tell(Integer.parseInt(serversData.get(randomPort)), ActorRef.noSender());
                }
        ).build();
    }
}
