import org.aspectj.lang.ProceedingJoinPoint;

public class AspectTest {

	public void intercept() {

	}

	public Object wrapAround(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("before");
		Object o = pjp.proceed();
		System.out.println("after");

		return o;

	}
}
