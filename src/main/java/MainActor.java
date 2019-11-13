import akka.actor.AbstractActor;
import akka.actor.ActorRef;

public class MainActor extends AbstractActor {
    private final static int NUM_ROUNDS = 5;
    private final ActorRef executor;
    private final ActorRef storage;


    @Override
    public Receive createReceive() {
        return null;
    }

    public MainActor() {
        this.executor = getContext().a;
        this.storage = storage;
    }
}
