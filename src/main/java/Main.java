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

    private static Socket frontend;
    private static Socket backend;
    private static HashMap<Pair<ZFrame, Long>, Pair<Integer, Integer>> hashStorage = new HashMap<>();

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
                                for (Map.Entry<Pair<ZFrame, Long>, Pair<Integer, Integer>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getValue().getKey() && index < entry.getValue().getValue() && isAlive(entry)) {
                                        found = true;
                                        getMessage.add(entry.getKey().getKey().duplicate());
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
                                for (Map.Entry<Pair<ZFrame, Long>, Pair<Integer, Integer>> entry : hashStorage.entrySet()) {
                                    if (index >= entry.getValue().getKey() && index < entry.getValue().getValue()) {
                                        found = true;
                                        setMessage.add(entry.getKey().getKey().duplicate());
                                        setMessage.add(address);
                                        setMessage.add("" + index);
                                        setMessage.add(value);
                                    }
                                }
                                send(setMessage, found);
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
                        if (checkFrame == NEW || checkFrame == NOTIFY) {
                            interval = message.popString().split();
                            hashStorage.put(new Pair<>(address, System.currentTimeMillis()), new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
                        }
                        if (checkFrame == SET || checkFrame == GET) {
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

    private static boolean isAlive(Map.Entry<Pair<ZFrame, Long>, Pair<Integer, Integer>> entry) {
        long now = System.currentTimeMillis();
        if ( now - entry.getKey().getValue() > DOUBLE_TIMEOUT) {
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