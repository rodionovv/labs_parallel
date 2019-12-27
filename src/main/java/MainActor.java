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
                           for int i in range for int i isss  


                        })
    }
}
