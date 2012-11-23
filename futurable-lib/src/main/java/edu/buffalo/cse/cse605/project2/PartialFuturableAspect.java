package edu.buffalo.cse.cse605.project2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PartialFuturableAspect {
    private static final transient Logger LOG = LoggerFactory.getLogger(FuturableAspect.class);

    private FuturableUtil utility;

    public Object interceptFuturableMethod(final ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("Before");

        final MethodSignature signature = (MethodSignature) pjp.getStaticPart().getSignature();
        Method method = signature.getMethod();
        PartialFuturable futurableAnnotation = method.getAnnotation(PartialFuturable.class);
        String executorName = futurableAnnotation.executor();

        AsyncTaskExecutor executor = utility.getExecutor(executorName);

        final Future<Object> future = executor.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    // proceed as a separate thread
                    return pjp.proceed();
                } catch (Throwable e) {
                    throw new ExecutionException(e);
                }
            }
        });

        final Class<?> classToEmulate = signature.getClass().getClassLoader().loadClass(signature.getReturnType().getName());
        Class<?>[] classes = {classToEmulate};

        final Object proxyObj = Proxy.newProxyInstance(signature.getClass().getClassLoader(), classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                LOG.debug("Giving to real object");

                // now we need to translate that to the real method call, on the hopefully completed real object

                String name = method.getName();

                Object[] adjustedArgs = (args == null) ? new Object[0] : args;

                Object realObject;

                try {
                    realObject = future.get();
                } catch (ExecutionException e) {
                    throw e.getCause().getCause(); // get rid of the ExecutionException x 2 and expose the original exception
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }

                // get the method argument we really want
                Method methodObj = ReflectionUtils.findMethod(realObject.getClass(), name, method.getParameterTypes());

                // call it and return it
                Object returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
                return returnValue;
            }
        });

        LOG.debug("After returned fake object");

        return proxyObj;

    }
    
    public void setFuturableUtil(FuturableUtil futurableUtil)
    {
    	utility = futurableUtil;
    }
}