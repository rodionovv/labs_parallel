import akka.japi.Pair;

public class GetMSG {


    Pair<String, Integer> msgPair;

    public GetMSG(Pair<String, Integer> msgPair) {
        this.msgPair = msgPair;
    }

    public Pair<String, Integer> getMsgPair() {
        return msgPair;
    }

    public int getCount() {
       return msgPair.second();
    }

    public String getValue() {
        return msgPair.first();
    }
}
