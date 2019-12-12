import org.zeromq.ZContext;
import org.zeromq.ZMQ;

class Main{



    public static void  main(String[] args) {

        try (ZContext context = new ZContext()) {
            ZMQ.Socket frontend = context.createSocket(ZMQ.ROUTER);
        }

    }
}