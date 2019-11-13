import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.RoundRobinPool;

public class MainActor extends AbstractActor {
    private final static int NUM_ROUNDS = 5;
    private final ActorRef executor;
    private final ActorRef storage;


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(
                Functions.class, pack -> {
                    int testLength = pack.getTests().length;
                    for (int i = 0; i < testLength; i++) {
                        executor.tell(new ExcuteMessage(i, pack), storage);
                    }
                }
        ).match()
    }

    public MainActor() {
        this.executor = getContext().actorOf(new RoundRobinPool(new RoundRobinPool(NUM_ROUNDS).props(Props.create(JSExecutorActor.class))));
        this.storage = getContext().actorOf(Props.create(StorageActor.class));
    }
}
