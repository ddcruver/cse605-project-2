import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClass {

	private static final transient Logger LOG = LoggerFactory.getLogger(TestClass.class);

    private InterceptInterface interceptClass;

    public TestClass(InterceptInterface interceptClass) {
        this.interceptClass = interceptClass;
        LOG.info("SUCCESS");
    }


    public void interceptMethod() {

    }

    public static void main(String[] args) {

    }


}
