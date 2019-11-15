import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import javafx.util.Pair;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JSExecutorActor extends AbstractActor {

    private final static String NASHORN = "nashorn";
    private final static String CORRECT = "correct";
    private final static String INCORRECT = "incorrect";


    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(ExcuteMessage.class, message -> {
            Pair<Integer, Functions> msg = message.getMsg();
            int index = msg.getKey();
            Functions functions = msg.getValue();
            Test test = functions.getTests()[index];
            ScriptEngine engine = new ScriptEngineManager().getEngineByName(NASHORN);
            try {
                engine.eval(functions.getScript());
            } catch (ScriptException e) {
                e.printStackTrace();
            }
            Invocable invocable = (Invocable) engine;
            String res = invocable.invokeFunction(functions.getFunctionName(), test.getParams()).toString();
            String check = INCORRECT;
            if (res.equals(test.getExpectedResult())) {
                check = CORRECT;
            }
            System.out.println(res);
            StorageMessage storageMessage = new StorageMessage(res, test.getExpectedResult(), check, test.getParams(), test.getTestName(), test.getFuncName());
            StorageCommand storageCommand = new StorageCommand(functions.getPackageID(), storageMessage);
            getSender().tell(storageCommand, ActorRef.noSender());
        }).build();
    }
}
