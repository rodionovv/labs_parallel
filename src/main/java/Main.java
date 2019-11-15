import akka.actor.ActorSystem;
class Main {

    private  static  final String TESTS_DIR = "/home/vasya/IdeaProjects/lab_parallel/tests";
    private static final String LOCALHOST = "localhost";
    private static final int PORT = 8080;
    private static final String ROUTES = " routes";


    public static void  main(String[] args)  throws Exception {
        Thread myThread = new RequestsThread(TESTS_DIR);
        myThread.start();

        ActorSystem system = ActorSystem.create(ROUTES);
        HttpServer server = new HttpServer(LOCALHOST, PORT, system);
        server.start();
    }

}

