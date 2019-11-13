import akka.actor.ActorRef;

public class MainActor {
    private final static int NUM_ROUNDS = 5;
    private final ActorRef executor;
    private final ActorRef storage;


    public MainActor() {
        this.executor = getContext().a;
        this.storage = storage;
    }
}
