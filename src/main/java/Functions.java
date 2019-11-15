import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Vector;

public class Functions {
    private final int packageId;
    private final String script;
    private final String functionName;
    private final Test[] tests;
    public static  Vector<Integer> packageIdList = new Vector<>();

    @JsonCreator
    public Functions(
            @JsonProperty("packageId") String packageId,
            @JsonProperty("jsScript") String script,
            @JsonProperty("functionName") String functionName,
            @JsonProperty("tests") Test[] tests) {
        this.packageId = Integer.parseInt(packageId);
        this.functionName = functionName;
        this.tests = tests;
        this.script = script;
        packageIdList.add(Integer.parseInt(packageId));
    }




    public String getScript() {
        return script;
    }


    public int getPackageID() {
        return packageId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Test[] getTests() {
        return tests;
    }

    public Test getTest(int i) {
        return tests[i];
    }
}
