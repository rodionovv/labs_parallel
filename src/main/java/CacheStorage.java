import org.zeromq.*;

public class CacheStorage {

    private static String str;
    private static int left;
    private static int right;

    public static void main(String[] args) {
        left = Integer.parseInt(args[1]);
        right = Integer.parseInt(args[2]);
        str = args[0].substring(left, right);
        try (ZContext ctx = new ZContext()) {
            ZMQ.Socket worker = ctx.createSocket(SocketType.DEALER);
            worker.connect("tcp://localhost:5560");
            long start = System.currentTimeMillis();

            ZMQ.Poller poller = ctx.createPoller(1);
            poller.register(worker, ZMQ.Poller.POLLIN);

            while (!Thread.currentThread().isInterrupted())
                if (System.currentTimeMillis() - start > 5000) {
                    ZMsg messageSend = new ZMsg();
                    System.out.println("5 secs later");
                    messageSend.addString(left + "-" + right);
                    messageSend.send(worker);
                    start = System.currentTimeMillis();
                }
            if (poller.pollin(0)) {
                ZMsg messageRecieved = ZMsg.recvMsg(worker);
                ZFrame content = messageRecieved.getLast();
                String s = content.toString();
                System.out.println(s);
                messageRecieved.send(worker);
            }
        }
    }
}
