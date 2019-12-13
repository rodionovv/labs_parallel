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

    private static HashMap<Pair<ZFrame, Long>, Pair<Integer, Integer>> hashStorage = new HashMap<>();

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
                        ZFrame address = message.unwrap();
                        for (ZFrame f : message) {
                            if (f.toString().equals("Get")) {
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
                                if (found) {
                                    getMessage.send(backend);
                                } else {
                                    ZMsg errorMessage = new ZMsg();
                                    errorMessage.wrap(address);
                                    errorMessage.add("Can't get hash at position " + index);
                                    errorMessage.send(frontend);
                                }
                                break;
                            }
                            if (f.toString().equals("Set")) {
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
                                if (found) {
                                    setMessage.send(backend);
                                } else {
                                    ZMsg errorMessage = new ZMsg();
                                    errorMessage.wrap(address);
                                    errorMessage.add("Can't change hash at position " + index);
                                    errorMessage.send(frontend);
                                }
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
                        ZMsg message = ZMsg.recvMsg(backend, 10000);
                        ZFrame address = message.pop();
                        String checkFrame = message.popString();
                        System.out.println(checkFrame);
                        String[] interval;
                        switch (checkFrame){
                            case "NEW":
                                interval = message.popString().split("-");
                                hashStorage.put(new Pair<>(address, System.currentTimeMillis()), new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
                                break;
                            case "NOTIFY":
                                interval = message.popString().split("-");
                                hashStorage.replace(new Pair<>(address,System.currentTimeMillis()), new Pair<>(Integer.parseInt(interval[0]), Integer.parseInt(interval[1])));
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

    private static boolean isAlive(Map.Entry<Pair<ZFrame, Long>, Pair<Integer, Integer>> entry) {
        long now = System.currentTimeMillis();
        if ( now - entry.getKey().getValue() > 10000) {
            hashStorage.remove(entry);
            return false;
        }
        return true;
    }

}