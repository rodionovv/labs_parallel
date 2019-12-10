package lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

class Main extends AllDirectives {

    private final static String LOCALHOST = "localhost";
    private final static int TIMEOUT = 10000;
    private final static String ERROR_MESSAGE = "Unable to establish connection to current url";
    private final static String HTTP_LOCALHOST = "http://localhost:";
    private final static String CODE_404 = "404";
    private final static String URL = "url";
    private final static String COUNT = "count";
    private final static String ROUTES = "routes";

    private static Http http;
    private static int newPort;
    private static ActorRef storageActor;

    public static void  main(String[] args)  throws IOException {


        Scanner in = new Scanner(System.in);
        newPort = in.nextInt();


        ActorSystem system = ActorSystem.create(ROUTES);
        storageActor = system.actorOf(Props.create(
                StorageActor.class
        ));


        try {
            Zoo zoo = new Zoo(newPort, storageActor);
            zoo.create();
        } catch (KeeperException | InterruptedException e) {
        }

        http =  Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        Main app = new Main();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system,materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(LOCALHOST, newPort),
                materializer
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }

    private Route createRoute() {
        return concat(
                get(
                        () -> parameter(
                                URL, url -> parameter(COUNT, count -> {
                                    System.out.println(url + " " + count);
                                    int parsedCount = Integer.parseInt(count);
                                    if (parsedCount != 0 ) {
                                        CompletionStage<HttpResponse> response = Patterns.ask(
                                                storageActor,
                                                new PortMessage(Integer.toString(newPort)),
                                                java.time.Duration.ofMillis(TIMEOUT)
                                        ).thenCompose(req -> makeRequestToServer(url,(int) req, parsedCount));
                                        return completeWithFuture(response);
                                    }
                                    try {
                                        return complete(makeRequest(url));
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                        return complete(ERROR_MESSAGE);
                                    }
                                }
                        )
                )
            )
        );
    }

    HttpResponse makeRequest(String url) throws ExecutionException, InterruptedException {
        return http.singleRequest(
                HttpRequest.create(url)
        ).toCompletableFuture().get();
    }

    CompletionStage<HttpResponse> makeRequestToServer(String url, int port, int parsedCount) {
        try {
            parsedCount -= 1;
            System.out.println(HTTP_LOCALHOST + port + "/&url=" + url + "&count=" + parsedCount);
            return http.singleRequest(
                    HttpRequest.create(HTTP_LOCALHOST + port + "/&url=" + url + "&count=" + parsedCount)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.completedFuture(HttpResponse.create().withEntity(CODE_404));
        }
    }

}