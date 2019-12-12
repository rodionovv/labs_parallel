import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.io.IOException;

class Main{



    public static void  main(String[] args) throws IOException {

        try (ZContext context = new ZContext()) {
            Socket frontend = context.createSocket(SocketType.ROUTER);
            Socket backend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5559");
            backend.bind("tcp://*:5560");
        }

    }
}