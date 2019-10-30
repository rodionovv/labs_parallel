import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;

class Main {



    public static void  main(String[] args) extends AllDirectives

    {
        ActorSystem system = ActorSystem.create("routes");
        final Http http =  Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
  
    }
}