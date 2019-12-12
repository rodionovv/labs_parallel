import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;
import zmq.poll.Poller;

import java.io.IOException;

class Main{



    public static void  main(String[] args) throws IOException {

        private static final String FRONTEND_ADRES =
        

        try (ZContext context = new ZContext()) {

            Socket frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5559");

            Socket backend = context.createSocket(SocketType.ROUTER);
            backend.bind("tcp://*:5560");

            ZMQ.Poller items = context.createPoller(2);
            items.register(frontend, ZMQ.Poller.POLLIN);
            items.register(backend, ZMQ.Poller.POLLIN);


        }

    }
}