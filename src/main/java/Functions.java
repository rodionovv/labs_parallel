import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Functions {
    private final int packageId;
    private final String script;
    private final String functionName;
    private final Test[] tests;

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

    public Test getTests(int i) {
        return tests[i];
    }
}
