import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.HashMap;
import java.util.Map;

public class MainActor extends AbstractActor {

    private HashMap<String, Map<Integer, Integer>> data = new HashMap<>();

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetMSG.class,
                        msg -> {
                           String url = msg.getUrl();
                           int count = msg.getCount();
                           if (data.containsKey(url) && data.get(url).containsKey(count)) {
                               getSender().tell(data.get(url).get(count), ActorRef.noSender());
                           } else {
                               getSender()
                           }

                        })
    }
}
