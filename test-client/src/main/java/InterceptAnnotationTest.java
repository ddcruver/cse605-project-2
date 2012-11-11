import edu.buffalo.cse.cse605.project2.Futurable;

import java.util.Arrays;
import java.util.List;

public class InterceptAnnotationTest {

    @Futurable
    public List<String> getList() throws InterruptedException {
        return Arrays.asList("hello", "world");
    }
}
