import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageActor extends AbstractActor {

    private HashMap<Integer, ArrayList<StorageMessage>> data = new HashMap<>();


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(
                Messages.class,
                req -> getSender().tell(
                        data.get(req.getPackageId()).toArray(),
                        ActorRef.noSender()
                )
        ).match(StorageCommand.class, msg -> {
            if (data.containsKey(msg.getPackageID())) {
                ArrayList<StorageMessage> tests = data.get(msg.getPackageID());
                tests.add(msg.getStorageMessage());
                data.put(msg.getPackageID(), tests);
            } else {
                ArrayList<StorageMessage> tests = new ArrayList<>();
                tests.add(msg.getStorageMessage());
                data.put(msg.getPackageID(), tests)
            }
        })
    }
}
