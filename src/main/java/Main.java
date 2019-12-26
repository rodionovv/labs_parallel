import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.*;
import akka.http.javadsl.server.AllDirectives;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.asynchttpclient.Dsl.asyncHttpClient;


class Main extends AllDirectives {

    public static final int PARALLELISM = 1;
    private static ActorRef maiActor;
    private static ActorMaterializer materializer;


    private static final String TEST_URL = "testUrl";
    private static final String COUNT = "count";
    private static final String EMPTY_STRING = "";
    private static final String ROUTES = "routes";
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;
    private static final String SERVER_MSG = "Server online at http://localhost:8080/\nPress RETURN to stop...";
    private static final String COUNT_ERROR_MSG = "No count parameter";
    private static final String URL_ERROR_MSG = "No URL parameter";
    public static final int MILLIS = 5000;

    public static void  main(String[] args)  throws IOException {
        ActorSystem system = ActorSystem.create(ROUTES);
        Http http =  Http.get(system);
        materializer = ActorMaterializer.create(system);
        maiActor = system.actorOf();
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

    private Flow<HttpRequest, HttpResponse, NotUsed> createRoute() throws NumberFormatException {
        return Flow.of(HttpRequest.class).map(
                req -> {
                    if (req.method() == HttpMethods.GET) {
                        Uri uri = req.getUri();
                        if (uri.path().equals("/")) {
                            String url = uri.query().getOrElse(TEST_URL, EMPTY_STRING);
                            String stringCount = uri.query().getOrElse(COUNT, EMPTY_STRING);
                            if (url.isEmpty()) {
                                return HttpResponse.create().withEntity(ByteString.fromString(URL_ERROR_MSG));
                            }
                            if (stringCount.isEmpty()) {
                                return HttpResponse.create().withEntity(ByteString.fromString(COUNT_ERROR_MSG));
                            }
                            Integer count = Integer.parseInt(stringCount);
                            Source<Pair<String, Integer>, NotUsed> src = Source.from(Collections.singleton(new Pair<>(url, count)));
                            Flow<Pair<String, Integer>, HttpResponse, NotUsed> sink = Flow.<Pair<String, Integer>>create()
                                    .map(pair -> new Pair<>(HttpRequest.create().withUri(pair.first()), pair.second()))
                                    .mapAsync(1, pair -> {
                                        return Patterns
                                                .ask(
                                                        maiActor,
                                                        new GetMSG(new javafx.util.Pair<>(url, count)),
                                                        Duration.ofMillis(MILLIS)
                                                ).thenCompose(
                                                        r -> {
                                                            //TODO:if
                                                            Sink<CompletionStage<Long>, CompletionStage<Integer>> fold = Sink
                                                                    .fold(0, (ac, element) -> {
                                                                        long el = element.toCompletableFuture().get();
                                                                        return Math.toIntExact(ac + el);
                                                                    });
                                                            return Source.from(Collections.singleton(pair))
                                                                    .toMat(
                                                                            Flow.<Pair<HttpRequest, Integer>>create()
                                                                                    .mapConcat(p -> Collections.nCopies(p.second(), p.first()))
                                                                                    .mapAsync(PARALLELISM, newReq -> {
                                                                                        return CompletableFuture.supplyAsync(() ->
                                                                                                System.currentTimeMillis()
                                                                                        ).thenCompose(start -> CompletableFuture.supplyAsync(() -> {
                                                                                            CompletionStage<Long> onResponse = asyncHttpClient()
                                                                                                    .prepareGet(newReq.getUri().toString())
                                                                                                    .execute()
                                                                                                    .toCompletableFuture()
                                                                                                    .thenCompose(ans ->
                                                                                                            CompletableFuture.completedFuture(System.currentTimeMillis() - start));
                                                                                            return onResponse;
                                                                                        }));
                                                                                    })
                                                                                    .toMat(fold, Keep.right()), Keep.right()).run(materializer);
                                                        }).thenCompose(sum -> {
                                                    Patterns.ask(maiActor, new msg);
                                                    double midVal = (double) sum / count;
                                                    return CompletableFuture.completedFuture(HttpResponse.create().withEntity(ByteString.fromString("" + midVal)));
                                                });
                                    });
                            CompletionStage<HttpResponse> res = src.via(sink).toMat(Sink.last(), Keep.right()).run(materializer);
                            return res.toCompletableFuture().get();
                        } else {
                            req.discardEntityBytes(materializer);
                            return HttpResponse.create().withEntity(ByteString.fromString(""));
                        }
                    } else {
                        req.discardEntityBytes(materializer);
                        return HttpResponse.create().withStatus(StatusCodes.NOT_FOUND).withEntity("");
                    }
                }
        );
    }
}