import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

public class Client {

    private static final String FRONTEND_ADDRESS = "tcp://*:5559";

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket req = context.createSocket(SocketType.REQ);
            req.connect("tcp://*:5559");
            
        }
    }
}
