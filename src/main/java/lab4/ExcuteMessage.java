package lab4;

import javafx.util.Pair;

public class ExcuteMessage {

    private Pair<Integer, Functions> msg;

    public ExcuteMessage(Functions functions, int i) {
        this.msg = new Pair<>(i, functions);
    }

    public Pair<Integer, Functions> getMsg() {
        return msg;
    }
}
