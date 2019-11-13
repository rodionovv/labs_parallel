import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Functions {
    private final int packageID;
    private final String script;
    private final String functionName;
    private final Test[] tests;

    @JsonCreator
    public Functions(
            @JsonProperty("packageID") String packageID,
            @JsonProperty("jsScript") String script,
            @JsonProperty("functionName") String functionName,
            @JsonProperty("tests") Test[] tests) {
        this.packageID = Integer.parseInt(packageID);
        this.functionName = functionName;
        this.tests = tests;
        this.script = script;
    }

    public String getScript() {
        return script;
    }


    public int getPackageID() {
        return packageID;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Test[] getTests() {
        return tests;
    }

    public Test getTests(int i) {
        return tests[i];
    }
}
