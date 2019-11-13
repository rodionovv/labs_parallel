public class StorageMessage {

    private final String result;
    private final String expectedResult;
    private final String testName;
    private final String checker;
    private final Object[] param;

    StorageMessage(String result,
                           String expectedResult,
                           String checker,
                           Object[] param,
                           String testName) {
        this.result = result;
        this.expectedResult = expectedResult;
        this.checker = checker;
        this.param = param;
        this.testName = testName;
    }


    public Object[] getParam() {
        return param;
    }

    public String getChecker() {
        return checker;
    }

    public String getTestName() {
        return testName;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getResult() {
        return result;
    }
}
