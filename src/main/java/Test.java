import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Test {

    private final String testName;
    private final String expectedResult;
    private final String realResult;
    private final String checker;
    private  final Object[] params;
    private final String funcName;





    @JsonCreator
    public Test(@JsonProperty("testName") String testName,
                @JsonProperty("expectedResult") String expectedResult,
                @JsonProperty("params") Object[] params,
                @JsonProperty("functionName") String funcName) {
        this.testName = testName;
        this.expectedResult = expectedResult;
        this.params = params;
        this.realResult = "";
        this.checker = "";
        this.funcName = funcName;
    }


    public Object[] getParams() {
        return params;
    }

    public String getChecker() {
        return checker;
    }

    public String getRealResult() {
        return realResult;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getTestName() {
        return testName;
    }

    public String getFuncName() {
        return funcName;
    }
}
