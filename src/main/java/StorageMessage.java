public class StorageMessage {

    private final String result;
    private final String expectedResult;
    private final String testName;
    private final String checker;
    private final Object[] param;

    private StorageMessage(String result,
                           String expectedResult,
                           String checker,
                           Object[] param,
                           String testName) {
        this.result = result;
        this.expectedResult = expectedResult;
        this.checker = checker;
        this.param = param;
        this.testName = testName;
        th
    }


}
