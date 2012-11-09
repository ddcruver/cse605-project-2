import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class Runner {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");

        TestClass bean = context.getBean("callingClass", TestClass.class);
        InterceptInterface intercept = context.getBean("interceptingClass", InterceptInterface.class);

        System.out.println("returned value:" + intercept.foo());

        InterceptAnnotationTest test = context.getBean("testAnnotationInterception", InterceptAnnotationTest.class);

        List<String> list = test.getList();
        System.out.println("returned list: " + list);

        ((ConfigurableApplicationContext) context).close();

    }
}
