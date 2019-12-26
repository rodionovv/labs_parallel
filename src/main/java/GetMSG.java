import javafx.util.Pair;

public class GetMSG {


    Pair<String, Integer> msgPair;


    public Pair<String, Integer> getMsgPair() {
        return msgPair;
    }

    public int getCount() {
       return msgPair.getValue();
    }

    public String getValue() {
        return msgPair.getKey();
    }
    

}
