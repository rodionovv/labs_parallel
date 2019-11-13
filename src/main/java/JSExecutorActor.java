import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import javafx.util.Pair;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

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
                engine.eval(functions.getScript());
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            Invocable invocable = (Invocable) engine;
            String res = invocable.invokeFunction(functions.getFunctionName(), test.getParams()).toString();
            String check = "incorrect";
            if (res.equals(test.getExpectedResult())) {
                check = "correct";
            }

            StorageMessage storageMessage = new StorageMessage
        })
    }
}
