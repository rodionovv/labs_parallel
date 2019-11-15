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

import java.io.FileReader;
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
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

class Main {

    private  static  final String TESTS_DIR = "/home/vasya/IdeaProjects/lab_parallel/tests";
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();


    public static void  main(String[] args)  throws Exception {




        Thread myThread = new RequestsThread(TESTS_DIR);
        myThread.start();

        ActorSystem system = ActorSystem.create("routes");
        HttpServer server = new HttpServer(LOCALHOST, PORT, system);
        server.start();
    }

}

