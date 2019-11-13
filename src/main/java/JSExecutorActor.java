import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import javafx.util.Pair;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSExecutorActor extends AbstractActor {


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(ExcuteMessage.class, message -> {
            Pair<Integer, Functions> msg = message.getMsg();
            int index = msg.getKey();
            Functions functions = msg.getValue();
            Test test = functions.getTests()[index];
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            try {
                
            }
        })
    }
}
