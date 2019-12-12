import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.Scanner;

public class Client {

    private static final String FRONTEND_ADDRESS = "tcp://localhost:5559";

    public static void main(String[] args) {
        try (ZContext context = new ZContext()) {
            ZMQ.Socket req = context.createSocket(SocketType.REQ);
            req.connect(FRONTEND_ADDRESS);
            Scanner in = new Scanner(System.in);
            while (true) {
                String[] command = in.nextLine().split(" ");
                if (command[0].equals("Stop")) {
                    break;
                }
                ZMsg message = new ZMsg();
                for (int i = 0; i < command.length; i++) {
                    System.out.println(command[i]);
                    message.add(command[i]);
                }
                message.send(req);
//                ZMsg response = ZMsg.recvMsg(req);
//                System.out.println(response.popString());
//                response.destroy();
                message.destroy();
            }
        }
    }
}
