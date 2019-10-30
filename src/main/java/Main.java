import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;

class Main {



    public static void  main(String[] args) {
        ActorSystem system = ActorSystem.create("routes");
        final Http http =  Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        MainHttp instance = new MainHttp(system);

    }
}