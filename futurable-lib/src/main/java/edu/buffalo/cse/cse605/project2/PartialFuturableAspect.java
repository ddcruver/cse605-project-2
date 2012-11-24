package edu.buffalo.cse.cse605.project2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PartialFuturableAspect {
    private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableAspect.class);

    private FuturableUtil utility;

    public Object interceptFuturableMethod(final ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("Before");

        final MethodSignature signature = (MethodSignature) pjp.getStaticPart().getSignature();
        Method method = signature.getMethod();
        PartialFuturable futurableAnnotation = method.getAnnotation(PartialFuturable.class);
        String executorName = futurableAnnotation.executor();

        AsyncTaskExecutor executor = utility.getExecutor(executorName);

        final Class<?> classToEmulate = signature.getClass().getClassLoader().loadClass(signature.getReturnType().getName());
        
        Class<?>[] parameterTypes = signature.getParameterTypes();
        
        LOG.debug("Return Type: {}", classToEmulate.getName());
        for(Class<?> parameterType : parameterTypes)
        {
        	LOG.error("Input Type: {}", parameterType.getName());
        }
        
        // TODO: This should identify which argument to use
        final Object realObject = pjp.getArgs()[0];
        
        Class<?>[] classes = {classToEmulate};
        
        HashingMethod hashingMethod = utility.getHashingMethod(realObject);
        InvocationHandler partialInvoker = new PartialFuturableInvocationHandler(realObject, utility);
        final Object proxyObj = Proxy.newProxyInstance(signature.getClass().getClassLoader(), classes, partialInvoker);
        
        final Object argumentsWithProxy[] = { proxyObj };
        
        LOG.debug("After returned fake object");

        final Future<Object> future = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    // proceed as a separate thread
                	// TODO: Must be changed so that the proxy object is added to the arguments
                    return pjp.proceed(argumentsWithProxy);
                } catch (Throwable e) {
                    throw new ExecutionException(e);
                }
            }
        });
        
        return proxyObj;
    }
    
	public void setFuturableUtil(FuturableUtil futurableUtil)
    {
    	utility = futurableUtil;
    }
}