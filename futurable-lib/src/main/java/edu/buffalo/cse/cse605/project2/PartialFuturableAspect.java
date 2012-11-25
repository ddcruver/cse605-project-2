package edu.buffalo.cse.cse605.project2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class PartialFuturableAspect {
    private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableAspect.class);

    private FuturableUtil utility;

    public Object interceptFuturableMethod(final ProceedingJoinPoint pjp) throws Throwable {

    	final String className = pjp.getTarget().getClass().getCanonicalName();
        final MethodSignature signature = (MethodSignature) pjp.getStaticPart().getSignature();
        final Method method = signature.getMethod();
        final String methodName = method.getName();
        
        LOG.debug("Calling {}#{}", className, methodName);
        
        PartialFuturable futurableAnnotation = method.getAnnotation(PartialFuturable.class);
        String executorName = futurableAnnotation.executor();
        
        AsyncTaskExecutor executor = utility.getExecutor(executorName);

        final Class<?> classToEmulate = signature.getClass().getClassLoader().loadClass(signature.getReturnType().getName());
        
        Class<?>[] parameterTypes = signature.getParameterTypes();
        
        LOG.debug("Return Type: {}", classToEmulate.getName());
        for(Class<?> parameterType : parameterTypes)
        {
        	LOG.debug("Input Type: {}", parameterType.getName());
        }
        
        final Object realObject;
        
        // If user provides an output container proxy it otherwise proxy the input container.
        if(pjp.getArgs()[1] == null)
        	realObject = pjp.getArgs()[0];
        else
        	realObject = pjp.getArgs()[1];
        
        Class<?>[] classes = {classToEmulate};
        final PartialFuturableAspectProxyInvocationHandler partialInvoker = new PartialFuturableAspectProxyInvocationHandler(realObject, utility);
        final Object proxyObj = Proxy.newProxyInstance(signature.getClass().getClassLoader(), classes, partialInvoker);
        
        final Object[] arguments = pjp.getArgs();
        arguments[1] = proxyObj;
        
        executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    return pjp.proceed(arguments);
                } catch (Throwable e) {
                    throw new ExecutionException(e);
                } finally
                {
                	LOG.trace("Setting Operation Complete for {}#{}", className, methodName);
                	partialInvoker.setOperationComplete();
                	LOG.trace("Getting Target {}#{}", className, methodName);
                	Object target = pjp.getTarget();
                	LOG.trace("Trying to Casting PartialFuturableComplete {}#{}", className, methodName);
                	PartialFuturableCompleted pfc = (PartialFuturableCompleted) target;
                	LOG.trace("Setting PartialFuturableComplete to true {}#{}", className, methodName);
                	pfc.setComplete(true);
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