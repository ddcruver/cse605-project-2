package edu.buffalo.cse.cse605.project2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created with IntelliJ IDEA.
 * User: jmlogan
 * Date: 11/9/12
 * Time: 2:41 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Futurable {
	String executor() default FuturableConstants.DEFAULT_TASK_EXECUTOR;
}
