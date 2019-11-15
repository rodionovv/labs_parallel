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

import java.io.IOException;
import java.util.concurrent.CompletionStage;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

class Main extends AllDirectives {

    private static ActorRef mainActor;
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static class Requests extends Thread {
        @Override
        public void run() {
            try {
                sleep(10000);
            } catch (InterruptedException e){}
            for (int i = 0; i < 3; i++) {
                try {
                    sleep(5000);
                } catch (InterruptedException e){}
                try {
                    sendPost();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                String result = sendGet();
                System.out.println(result);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void  main(String[] args)  throws Exception {


        Thread myThread = new Requests();
        myThread.start();

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
         System.out.println("here");

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

    private static void sendPost() throws Exception {

        String result = "";
        HttpPost post = new HttpPost("http://localhost:8080/");
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"packageId\":\"11\",");
        json.append("\"jsScript\":\"var divideFn = function(a,b) {retrurn a/b}\",");
        json.append("\"packageId\":\"11\",");
        json.append("\"functionName\":\"divideFn\",");
        json.append("\"tests\":[");
        json.append("\t{\"testName\":\"test1\",");
        json.append("\t\"expectedResult\":\"2.0\",");
        json.append("\t\"params\":\"[2,1]\",");
        json.append("\t},");
        json.append("\t{\"testName\":\"test2\",");
        json.append("\t\"expectedResult\":\"2.0\",");
        json.append("\t\"params\":\"[4,2]\",");
        json.append("\t},");
        json.append("]");
        json.append("}");
        StringEntity requestEntity  = new StringEntity(
                json.toString(),
                ContentType.APPLICATION_JSON
        );
        post.setEntity(new StringEntity(json.toString()));
        post.setEntity(requestEntity);
        httpClient.execute(post);
    }

    private  static String sendGet() throws Exception {
        String result = "";
        HttpGet request = new HttpGet("GET http://localhost:8080/?packageId=11");
        try (CloseableHttpResponse response = httpClient.execute(request)){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                result = EntityUtils.toString(entity);
            }
        }
        return result;
    }
}

