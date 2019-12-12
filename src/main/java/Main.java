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
    private static ZFrame emptyFrame;

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
                    while (true) {
                        ZMsg message = ZMsg.recvMsg(frontend);
                        System.out.println(message.toString());
                        ZFrame address = message.pop();
                        emptyFrame = message.pop();
                        for (ZFrame f : message) {
                            if (f.toString().equals("Get")) {
                                System.out.println("in first if");
                                ZMsg getMessage = new ZMsg();
                                int index = Integer.parseInt(message.getLast().toString());
                                for (Map.Entry<ZFrame, Pair<Integer, Integer>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getValue().getKey() && index <= entry.getValue().getValue()) {

                                        System.out.println("in second if");
                                        getMessage.add(entry.getKey());
                                        getMessage.add(address);
                                        getMessage.add(message.getLast());
                                        break;
                                    }
                                }
                                getMessage.send(backend);
                                break;
                            }
                            if (f.toString().equals("Set")) {
                                ZMsg setMessage = new ZMsg();
                                int index = Integer.parseInt(message.getLast().toString());
                                for (Map.Entry<ZFrame, Pair<Integer, Integer>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getValue().getKey() && index < entry.getValue().getValue()) {
                                        setMessage.add(entry.getKey());
                                        setMessage.add(address);
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
                        System.out.println(checkFrame);
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
                                ZMsg responseMessage = new ZMsg();
                                responseMessage.add(message.pop());
                                responseMessage.add(emptyFrame);
                                responseMessage.add(message.pop());
                                responseMessage.send(frontend);
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