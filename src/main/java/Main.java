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

class Main extends AllDirectives {

    private static ActorRef mainActor;
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static class Requests extends Thread {
        @Override
        public void run() {
            try {
                sleep(5000);
            } catch (InterruptedException e){}
            for (int i = 0; i < 3; i++) {
                try {
                    sleep(1000);
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

    }



    @SuppressWarnings("unchecked")
    private static void sendPost() throws Exception {

        String result = "";
        HttpPost post = new HttpPost("http://localhost:8080/");
        JSONParser jsonParser = new JSONParser();
        try(FileReader reader = new FileReader("/home/vasya/IdeaProjects/lab_parallel/tests.json")){
            Object obj = jsonParser.parse(reader);

            StringEntity requestEntity  = new StringEntity(
                    obj.toString(),
                    ContentType.APPLICATION_JSON
            );
            post.setEntity(new StringEntity(obj.toString()));
            post.setEntity(requestEntity);
            try (CloseableHttpResponse response = httpClient.execute(post)){
                result = EntityUtils.toString(response.getEntity());
                System.out.println(result);
            }
        }

    }

    private  static String sendGet() throws Exception {
        String result = "here";
        HttpGet request = new HttpGet("http://localhost:8080/?packageID=11");
        try (CloseableHttpResponse response = httpClient.execute(request)){
            HttpEntity entity = response.getEntity();
            if (entity != null){
                result = EntityUtils.toString(entity);
            }
        }
        return result;
    }
}

