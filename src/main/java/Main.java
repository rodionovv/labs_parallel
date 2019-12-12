import javafx.util.Pair;
import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class Main{

    private static final String FRONTEND_ADDRESS = "tcp://localhost:5559";
    private static final String BACKEND_ADDRESS = "tcp://localhost:5560";

    private static HashMap<ZFrame, Pair<Integer, Integer>> hashStorage = new HashMap<>();

    public static void  main(String[] args) {

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
                        for (ZFrame f : message) {
                            if (f.toString().equals("Get")) {
                                ZMsg getMessage = new ZMsg();
                                int index = Integer.parseInt(message.getLast().toString());
                                for (Map.Entry<ZFrame, Pair<Integer, Integer>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getValue().getKey() && index < entry.getValue().getValue()) {
                                        getMessage.add(entry.getKey());
                                        getMessage.add(message.getLast());
                                        break;
                                    }
                                }
                                System.out.println(getMessage.toString());
                                getMessage.send(backend);
                                System.out.println("after send");
                                break;
                            }
                            if (f.toString().equals("Set")) {
                                ZMsg setMessage = new ZMsg();
                                int index = Integer.parseInt(message.getLast().toString());
                                for (Map.Entry<ZFrame, Pair<Integer, Integer>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getValue().getKey() && index < entry.getValue().getValue()) {
                                        setMessage.add(entry.getKey());
                                        setMessage.add(message.pollLast());
                                        setMessage.add(message.pollLast());
                                    }
                                }
                            }
                        }
                        more = frontend.hasReceiveMore();
                        if (!more) {
                            break;
                        }
                    }
                }
                if (items.pollin(1)) {
                    while (true) {
                        ZMsg message = ZMsg.recvMsg(backend);
                        ZFrame address = message.pop();
                        String checkFrame = message.popString();
                        String[] interval;
                        switch (checkFrame){
                            case "NEW":
                                interval = message.popString().split("-");
                                hashStorage.put(address, new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
                                break;
                            case "NOTIFY":
                                interval = message.popString().split("-");
                                hashStorage.replace(address, new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
                                break;
                            case "GET":
                                message.send(frontend);
                                break;
                        }
                        more = backend.hasReceiveMore();
                        if (!more) {
                            break;
                        }
                    }
                }
            }
        }
    }
}