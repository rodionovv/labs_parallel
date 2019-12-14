import javafx.util.Pair;
import org.zeromq.ZContext;
import org.zeromq.*;
import org.zeromq.ZMQ.Socket;

import java.util.HashMap;
import java.util.Map;

class Main{

    private static final String FRONTEND_ADDRESS = "tcp://localhost:5559";
    private static final String BACKEND_ADDRESS = "tcp://localhost:5560";
    private static  final String GET = "Get";
    private static  final String SET = "Set";
    private static  final String NEW = "NEW";
    private static  final String NOTIFY = "NOTIFY";
    private static final int DOUBLE_TIMEOUT = 10000;
    private static final String DASH = "-";

    private static Socket frontend;
    private static Socket backend;
    private static HashMap< Pair<Integer, Integer>, Pair<ZFrame, Long>> hashStorage = new HashMap<>();

    public static void  main(String[] args) {

        try (ZContext context = new ZContext()) {

            frontend = context.createSocket(SocketType.ROUTER);
            frontend.bind(FRONTEND_ADDRESS);

            backend = context.createSocket(SocketType.ROUTER);
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
                        ZFrame address = message.unwrap();
                        for (ZFrame f : message) {
                            if (f.toString().equals(GET)) {
                                ZMsg getMessage = new ZMsg();
                                boolean found = false;
                                int index = Integer.parseInt(message.getLast().toString());
                                for (Map.Entry< Pair<Integer, Integer>, Pair<ZFrame, Long>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getKey().getKey() && index < entry.getKey().getValue() && isAlive(entry)) {
                                        found = true;
                                        getMessage.add(entry.getValue().getKey().duplicate());
                                        getMessage.add(address);
                                        getMessage.add(message.getLast());
                                        break;
                                    }
                                }
                                send(getMessage, found , address, index);
                                break;
                            }
                            if (f.toString().equals(SET)) {
                                ZMsg setMessage = new ZMsg();
                                ZFrame value = message.pollLast();
                                boolean found = false;
                                int index = Integer.parseInt(message.getLast().toString());
                                for (Map.Entry< Pair<Integer, Integer>, Pair<ZFrame, Long>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getKey().getKey() && index < entry.getKey().getValue() && isAlive(entry)) {
                                        found = true;
                                        setMessage.add(entry.getValue().getKey().duplicate());
                                        setMessage.add(address);
                                        setMessage.add("" + index);
                                        setMessage.add(value);
                                    }
                                }
                                send(setMessage, found, address, index);
                                break;
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

                        switch (checkFrame) {
                            case NEW:
                                interval = message.popString().split(DASH);
                                hashStorage.put(new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])), new Pair<>(address, System.currentTimeMillis()));
                                break;
                            case NOTIFY:
                                interval = message.popString().split(DASH);
                                hashStorage.replace(new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])), new Pair<>(address, System.currentTimeMillis()));
                                break;
                            default:
                                message.wrap(message.pop());
                                message.send(frontend);
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

    private static boolean isAlive(Map.Entry<Pair<Integer, Integer>, Pair<ZFrame, Long>> entry) {
        long now = System.currentTimeMillis();
        if ( now - entry.getValue().getValue() > DOUBLE_TIMEOUT) {
            hashStorage.remove(entry);
            return false;
        }
        return true;
    }

    private static void send(ZMsg message, boolean found, ZFrame address, int index) {
        if (found) {
            message.send(backend);
        } else {
            ZMsg errorMessage = new ZMsg();
            errorMessage.wrap(address);
            errorMessage.add("Can't access hash at position " + index);
        }
    }
}