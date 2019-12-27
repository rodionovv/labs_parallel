import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class MainActor extends AbstractActor {



    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(GetMSG.class,
                        msg -> {
                           String url = msg.getUrl();
                           int count = msg.getCount();
                           if ()
                        })
    }
}

