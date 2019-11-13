import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Test {

    private final String testName;
    private final String expectedResult;
    private final String realResult;
    private final String checker;
    private  final Object[] params;




    @JsonCreator
    public Test(@JsonProperty("testName") String testName,
                @JsonProperty("expectedResult") String expectedResult,
                @JsonProperty("params") String params) {

        this.testName = testName;
        this.expectedResult = expectedResult;
        this.params = params;
    }
}
