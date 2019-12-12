import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import java.io.IOException;

class Main{



    public static void  main(String[] args) throws IOException {

        try (ZContext context = new ZContext()) {
            ZMQ.Socket frontend = context.createSocket(ZMQ.ROUTER);
            ZMQ.Socket backend = context.createSocket(ZMQ.ROUTER);
        }

    }
}