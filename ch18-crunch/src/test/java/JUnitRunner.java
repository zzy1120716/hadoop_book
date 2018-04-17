import com.zzy.hadoopbook.crunch.PrimitiveOperationsTest;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JUnitRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(PrimitiveOperationsTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        if (result.wasSuccessful()) {
            System.out.println("Success!");
        }
    }
}
