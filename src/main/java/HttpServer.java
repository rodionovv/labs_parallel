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
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class HttpServer {

    private final String host;
    private final int port;
    private final ActorSystem system;
    private final ActorRef mainActor;

    private final static String PACKAGE_ID = "packageID";
    private final static int TIMEOUT = 5000;
    private final static String POST_MESSAGE = "message send";

    HttpServer(String host, int port, ActorSystem system){
        this.host = host;
        this.port = port;
        this.system = system;
        this.mainActor = system.actorOf(Props.create(MainActor.class));
    }

    public void start() throws IOException {

        final Http http =  Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Main app = new Main();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = createRoute().flow(system,materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(this.host, this.port),
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
                        () -> parameter(PACKAGE_ID, (packageID) -> {
                                    if (Functions.packageIdList.contains(packageID)) {
                                        Future<Object> result = Patterns.ask(
                                                mainActor,
                                                new Messages(Integer.parseInt(packageID)),
                                                TIMEOUT);
                                        return StatusCodes.
                                        return completeOKWithFuture(result, Jackson.marshaller());
                                    }
                                }
                        )
                ),
                post(
                        () -> entity(Jackson.unmarshaller(Functions.class),
                                msg -> {
                                    mainActor.tell(msg, ActorRef.noSender());
                                    return complete(POST_MESSAGE);
                                }
                        )));
    }

}
