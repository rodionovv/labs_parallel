import javafx.util.Pair;
import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.io.IOException;
import java.util.HashMap;

class Main{

    private static final String FRONTEND_ADDRESS = "tcp://localhost:5559";
    private static final String BACKEND_ADDRESS = "tcp://localhost:5560";

    private static HashMap<ZFrame, Pair<Integer, Integer>> hashStorage = new HashMap<>();

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
                        System.out.println(message.pop().toString());
                        more = frontend.hasReceiveMore();
                        message.send(backend);
                        if (!more) {
                            break;
                        }
                    }
                }
                if (items.pollin(1)) {
                    while (true) {
                        ZMsg message = ZMsg.recvMsg(backend);
                        more = backend.hasReceiveMore();
                        ZFrame address = message.pop();
                        String[] interval = message.popString().split("-");
                        hashStorage.put(address, new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
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