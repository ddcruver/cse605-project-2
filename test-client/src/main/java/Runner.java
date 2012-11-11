import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Runner {
	
	private static final transient Logger LOG = LoggerFactory.getLogger(Runner.class);
	
    public static void main(String[] args) throws InterruptedException {
    	LOG.info("Creating Application Context");
        ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");

        TestClass bean = context.getBean("callingClass", TestClass.class);
        InterceptInterface intercept = context.getBean("interceptingClass", InterceptInterface.class);

        LOG.info("Returned value: {}", intercept.foo());

        InterceptAnnotationTest test = context.getBean("testAnnotationInterception", InterceptAnnotationTest.class);

        LOG.debug("Calling future method");
        List<String> list = test.getList();
        LOG.debug("Returning from future method");
        
        LOG.info("Returned list: {}", list);

        ((ConfigurableApplicationContext) context).close();
        
        LOG.debug("Runner Done");
    }
}
