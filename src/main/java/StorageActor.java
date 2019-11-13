import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageActor extends AbstractActor {

    private HashMap<Integer, ArrayList<StorageMessage>> data = new HashMap<>();


    @Override
    public Receive createReceive() {
        return null;
    }
}
