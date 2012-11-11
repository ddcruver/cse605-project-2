import edu.buffalo.cse.cse605.project2.Futureable;

import java.util.Arrays;
import java.util.List;

public class InterceptAnnotationTest {

    @Futureable
    public List<String> getList() throws InterruptedException {
    	Thread.sleep(2000);
        return Arrays.asList("hello", "world");
    }
}
