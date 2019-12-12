import org.zeromq.*;

public class CacheStorage {
    private static String str;
    private static String left;
    private static String right;


    private static final String BACKEND_ADDRESS = "tcp://localhost:5560";

    public static void main(String[] args) {
        left = args[1];
        right = args[2];
        str = args[0].substring(Integer.parseInt(left), Integer.parseInt(right));
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket dealer = ctx.createSocket(SocketType.DEALER);
            dealer.connect(BACKEND_ADDRESS);
            long start = System.currentTimeMillis();

            ZMQ.Poller poller = ctx.createPoller(2);
            poller.register(dealer, ZMQ.Poller.POLLIN);

            while (!Thread.currentThread().isInterrupted()) {
                if (System.currentTimeMillis() - start > 5000) {
                    ZMsg messageSend = new ZMsg();
                    messageSend.addString(left + "-" + right);
                    messageSend.send(dealer);
                    start = System.currentTimeMillis();
                }
                if (poller.pollin(0)) {
                    ZMsg messageReceive = ZMsg.recvMsg(dealer);
                    for (ZFrame f : messageReceive) {
                        System.out.println(f.toString());
                    }
                    messageReceive.send(dealer);
                }
            }
        }
    }
}
