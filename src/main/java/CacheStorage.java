import org.zeromq.*;

public class CacheStorage {
    private static String str;
    private static String left;
    private static String right;

    public static void main(String[] args) {
        left = args[1];
        right = args[2];
        str = args[0].substring(Integer.parseInt(left), Integer.parseInt(right));
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket worker = ctx.createSocket(SocketType.DEALER);
            worker.connect("tcp://localhost:5560");
            long start = System.currentTimeMillis();

            ZMQ.Poller poller = ctx.createPoller(2);
            poller.register(worker, ZMQ.Poller.POLLIN);

            while (!Thread.currentThread().isInterrupted()) {
                if (System.currentTimeMillis() - start > 5000) {
                    ZMsg messageSend = new ZMsg();
                    System.out.println("5 secs later");
                    messageSend.addString(left + "-" + right);
                    messageSend.send(worker);
                    start = System.currentTimeMillis();
                }
                if (poller.pollin(0)) {
                    //parse msg
                    ZMsg messageRecieved = ZMsg.recvMsg(worker);
                    ZFrame content = messageRecieved.getLast();
                    String s = content.toString();
                    System.out.println(s);
                    messageRecieved.send(worker);
                }
            }
        }
    }
}
