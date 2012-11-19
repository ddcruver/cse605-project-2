package edu.buffalo.cse.cse605.project2;

import com.google.common.collect.MapMaker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuturableAspect implements ApplicationContextAware {
    private static final transient Logger LOG = LoggerFactory.getLogger(FuturableAspect.class);

    private AsyncTaskExecutor defaultExecutor;

    private ApplicationContext applicationContext;

    private FuturableAspect(AsyncTaskExecutor executor) {
        defaultExecutor = executor;
    }


    Object realObject;
    boolean wasCompleted = false;

    public Object interceptFuturableMethod(final ProceedingJoinPoint pjp) throws Throwable {
        LOG.debug("Before");

        final MethodSignature signature = (MethodSignature) pjp.getStaticPart().getSignature();
        Method method = signature.getMethod();
        Futurable futurableAnnotation = method.getAnnotation(Futurable.class);
        String executorName = futurableAnnotation.executor();

        AsyncTaskExecutor executor;

        if (executorName.equals(FuturableConstants.DEFAULT_TASK_EXECUTOR)) {
            executor = defaultExecutor;
        } else {
            executor = applicationContext.getBean(executorName, ThreadPoolTaskExecutor.class);
        }

        final Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                try {
                    // proceed as a separate thread
                    return pjp.proceed();
                } catch (Throwable e) {
                    throw new ExecutionException(e);
                }
            }
        };

        final Future<Object> future = executor.submit(callable);

        final Class<?> classToEmulate = signature.getClass().getClassLoader().loadClass(signature.getReturnType().getName());
        Class<?>[] classes = {classToEmulate};

        final Object proxyObj = Proxy.newProxyInstance(signature.getClass().getClassLoader(), classes, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                LOG.debug("Giving to real object");

                // now we need to translate that to the real method call, on the hopefully completed real object

                String name = method.getName();

                Object[] adjustedArgs = (args == null) ? new Object[0] : args;

                if (!wasCompleted) {
                    try {
                        wasCompleted = true;
                        // if it hasn't started executing yet, and we need the results, execute it in the current thread instead
                        if (!future.isDone() && future.cancel(false)) {
                            LOG.debug("Cancelling the execution of the result, and executing locally.");
                            realObject = callable.call();
                        } else {
                            try {
                                LOG.debug("Waiting for execution to finish on the thread pool...");
                                // we throw an ExecutionException, and the Executor will wrap that in another ExecutionException
                                // we want to expose the real exception cause, so that a client will be able to handle it
                                realObject = future.get();
                            } catch (InterruptedException e) {
                                wasCompleted = false;
                                throw e;
                            }
                        }
                    } catch (ExecutionException e) {
                        throw e.getCause(); // "unwrap" the execution exception that we threw, and expose the real exception
                    } catch (InterruptedException e) {
                        throw new IllegalStateException(e);
                    }
                }

                assert realObject != null;

                // get the method argument we really want
                Method methodObj = ReflectionUtils.findMethod(realObject.getClass(), name, method.getParameterTypes());

                // call it and return it
                Object returnValue = null;

                try {
                    methodObj.setAccessible(true);
                    returnValue = methodObj.invoke(realObject, adjustedArgs);
                } catch (Exception ex) {
                    LOG.error("Caught exception.", ex);
                }
                return returnValue;
            }
        });

        proxyToFutureMap.put(proxyObj, future);

        LOG.debug("After returned fake object");

        return proxyObj;

    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    private Map<Object, Future<?>> proxyToFutureMap = new MapMaker().weakKeys().weakValues().makeMap();

    public Object interceptFuturableQueue(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Queue originalQueue = (Queue) proceedingJoinPoint.proceed();

        // need to go from proxy -> future
        List<Future<?>> futureList = new ArrayList<Future<?>>();

        for (Object o : originalQueue) {
            Future<?> future = proxyToFutureMap.remove(o); // help clean up the map, we will only need this value at most once, just remove it
            assert future != null : "Future object was not found in the proxy->future map!";
            futureList.add(future);
        }

        return new ProxyQueue<Object>(futureList);
    }
}
