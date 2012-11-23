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
import java.util.HashMap;
import java.util.Map;
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
        
        Class<?>[] parameterTypes = signature.getParameterTypes();
        
      
        LOG.debug("Return Type: {}", classToEmulate.getName());
        
        for(Class<?> parameterType : parameterTypes)
        {
        	LOG.error("Input Type: {}", parameterType.getName());
        }
        
        // TODO: This should identify which argument to use
        final Object realObject = pjp.getArgs()[0];
        
        Class<?>[] classes = {classToEmulate};
        
        final Object proxyObj = Proxy.newProxyInstance(signature.getClass().getClassLoader(), classes, new InvocationHandler() {

        	final Map<String, Boolean> hasCompletedMap = new HashMap<String, Boolean>();
        	
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                LOG.debug("Giving to real object");

                // now we need to translate that to the real method call, on the hopefully completed real object

                String name = method.getName();
                PartialFuturableGetter getter = method.getAnnotation(PartialFuturableGetter.class);
                PartialFuturableSetter setter = method.getAnnotation(PartialFuturableSetter.class);
                
                Object[] adjustedArgs = (args == null) ? new Object[0] : args;
                
                // get the method argument we really want
                Method methodObj = ReflectionUtils.findMethod(realObject.getClass(), name, method.getParameterTypes());

                Object returnValue;
                
                if(getter != null)
                {
                	// TODO: This is placeholder until actual blocking mechanism is in place
                	Thread.sleep(1000);
                	returnValue = handleGetter(realObject, method, methodObj, adjustedArgs);
                }
                else if(setter != null)
                {
                	// TODO: A blocking mechanism should be placed here
                	returnValue = handleSetter(realObject, method, methodObj, adjustedArgs);
                }
                else
                {
                	LOG.debug("Executing normal method");
                	returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
                }
                
                return returnValue;            
            }
           
            protected Object handleSetter(Object realObject, Method method, Method methodObj, Object[] adjustedArgs)
            {
            	LOG.debug("Executing setter method");
            	Object returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
        		return returnValue;
        	}

        	protected Object handleGetter(Object realObject, Method method, Method methodObj, Object[] adjustedArgs)
        	{
        		LOG.debug("Executing getter method");
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