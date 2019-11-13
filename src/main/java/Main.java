import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import java.util.concurrent.CompletionStage;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

class Main extends AllDirectives {

    private static ActorRef mainActor;
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;

    public static void  main(String[] args)  throws Exception {
        ActorSystem system = ActorSystem.create("routes");
        mainActor = system.actorOf(Props.create(MainActor.class));
        final Http http =  Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Main app = new Main();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system,materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(LOCALHOST, PORT),
                materializer);
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return concat(
                get(
                        () -> parameter("packageID", (packageID) -> {
                                    Future<Object> result = Patterns.ask(
                                            mainActor,
                                            new Messages(Integer.parseInt(packageID)),
                                            5000);
                                    return completeOKWithFuture(result, Jackson.marshaller());
                                }
                        )
                ),
                post(
                        () -> entity(Jackson.unmarshaller(Functions.class),
                                msg -> {
                                    mainActor.tell(msg, ActorRef.noSender());
                                    return  complete("message posted" );
                                }
                )));
    }
}