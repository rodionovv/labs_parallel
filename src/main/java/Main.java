import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpMethods;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Uri;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Complete;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

class Main extends AllDirectives {


    private static final String ROUTES = "routes";
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;
    private static final String SERVER_MSG = "Server online at http://localhost:8080/\nPress RETURN to stop...";

    public static void  main(String[] args)  throws IOException {
        ActorSystem system = ActorSystem.create(ROUTES);
        final Http http =  Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Main app = new Main();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(LOCALHOST, PORT),
                materializer
        );
        System.out.println(SERVER_MSG);
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Flow<HttpRequest, HttpResponse, NotUsed> createRoute() {
        return Flow.of(HttpRequest.class).map(
                req -> {
                    if (req.method() == HttpMethods.GET) {
                        Uri uri = req.getUri();
                        if (uri.path().equals("/")) {
                            String url = uri.query().getOrElse("testUrl", "");
                            String count = uri.query().getOrElse("count", "");
                            if (url.isEmpty()) {
                                
                            }
                        }
                    }
                }
        );
    }
}