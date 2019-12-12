import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;
import zmq.poll.Poller;

import java.io.IOException;

class Main{



    public static void  main(String[] args) throws IOException {

        try (ZContext context = new ZContext()) {

            Socket frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind("tcp://*:5559");

            Socket backend = context.createSocket(SocketType.ROUTER);
            backend.bind("tcp://*:5560");

            Poller items = new Poller(2);
            items.register(frontend, Poller.POLLIN);

        }

    }
}