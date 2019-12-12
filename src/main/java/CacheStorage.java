import org.zeromq.*;

public class CacheStorage {
    private static StringBuilder str;
    private static int left;
    private static int right;


    private static final String BACKEND_ADDRESS = "tcp://localhost:5560";

    public static void main(String[] args) {
        left = Integer.parseInt(args[1]);
        right = Integer.parseInt(args[2]);
        str = new StringBuilder(args[0].substring(left, right));
        try (ZContext context = new ZContext()) {
            ZMQ.Socket dealer = context.createSocket(SocketType.DEALER);
            dealer.connect(BACKEND_ADDRESS);
            long start = System.currentTimeMillis();

            ZMQ.Poller poller = context.createPoller(1);
            poller.register(dealer, ZMQ.Poller.POLLIN);
            ZMsg messageSend = new ZMsg();
            messageSend.add("NEW");
            messageSend.addString(left + "-" + right);
            messageSend.send(dealer);

            while (!Thread.currentThread().isInterrupted()) {
                poller.poll(1);
                if (System.currentTimeMillis() - start > 5000) {
                    messageSend = new ZMsg();
                    messageSend.add("NOTIFY");
                    messageSend.addString(left + "-" + right);
                    messageSend.send(dealer);
                    start = System.currentTimeMillis();
                }
                if (poller.pollin(0)) {
                    ZMsg messageReceive = ZMsg.recvMsg(dealer);
                    System.out.println(messageReceive.toString());
                    if (messageReceive.size() == 2) {
                        ZMsg responseMessage = new ZMsg();
                        int index = Integer.parseInt(messageReceive.pollLast().toString());
                        responseMessage.add("GET");
                        ZFrame address = messageReceive.pop();
                        responseMessage.add(address);
                        responseMessage.add("" + str.charAt(index - left));
                        System.out.println(responseMessage.toString());
                        responseMessage.send(dealer);
                    }
                    if (messageReceive.size() == 3) {
                        ZMsg responseMessage = new ZMsg();
                        String value = messageReceive.pollLast().toString();
                        int index = Integer.parseInt(messageReceive.pollLast().toString());
                        responseMessage.add("SET");
                        ZFrame address = messageReceive.pop();
                        responseMessage.add(address);
                        System.out.println(str);
                        str.setCharAt(index, value.charAt(0));
                        System.out.println(str);
                        responseMessage.add("Value changed");
                        responseMessage.send(dealer);
                    }
                }
            }
        }
    }
}
