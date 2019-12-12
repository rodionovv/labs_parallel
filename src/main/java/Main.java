import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.io.IOException;

class Main{

    private static final String FRONTEND_ADDRESS = "tcp://*:5559";
    private static final String BACKEND_ADDRESS = "tcp://*:5560";

    public static void  main(String[] args) throws IOException {

        try (ZContext context = new ZContext()) {

            Socket frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind(FRONTEND_ADDRESS);

            Socket backend = context.createSocket(SocketType.ROUTER);
            backend.bind(BACKEND_ADDRESS);

            ZMQ.Poller items = context.createPoller(2);
            items.register(frontend, ZMQ.Poller.POLLIN);
            items.register(backend, ZMQ.Poller.POLLIN);
            boolean more;

            while (!Thread.currentThread().isInterrupted()) {
                items.poll();
                if (items.pollin(0)) {
                    System.out.println("in frontend");
                    while (true) {
                        ZMsg message = ZMsg.recvMsg(frontend);
                        more = frontend.hasReceiveMore();
                        message.send(backend);
                        if (!more) {
                            break;
                        }
                    }
                }
                if (items.pollin(1)) {
                    System.out.println("in backend");
                    while (true) {
                        ZMsg message = ZMsg.recvMsg(backend);
                        more = backend.hasReceiveMore();
                        for (ZFrame frame : message) {
                            System.out.println(frame);
                        }
                        message.send(frontend);
                        if (!more) {
                            break;
                        }
                    }
                }
            }
        }
    }
}