package edu.buffalo.cse.cse605.project2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;

public class FuturableUtil implements ApplicationContextAware
{

	private static final transient Logger LOG = LoggerFactory.getLogger(FuturableUtil.class);

	private ApplicationContext applicationContext;

	private AsyncTaskExecutor defaultExecutor;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException
	{
		applicationContext = context;
	}

	public void setDefaultExecutor(AsyncTaskExecutor executor)
	{
		defaultExecutor = executor;
	}

	public AsyncTaskExecutor getExecutor(String executorName)
	{
		AsyncTaskExecutor executor;

		if (executorName.equals(FuturableConstants.DEFAULT_TASK_EXECUTOR))
		{
			executor = defaultExecutor;
		} else
		{
			executor = applicationContext.getBean(executorName, ThreadPoolTaskExecutor.class);
		}
		return executor;
	}

	public Object executeMethod(Object realObject, Method methodObj, Object[] adjustedArgs)
	{
		Object returnValue = null;

		try
		{
			methodObj.setAccessible(true);
			returnValue = methodObj.invoke(realObject, adjustedArgs);
		} catch (Exception ex)
		{
			// TODO: Must handle the re-throwing of exceptions that may actually be thrown by invoking function.
			LOG.error("Caught exception.", ex);
		}

		return returnValue;
	}

	public HashingMethod getHashingMethod(Object realObject)
	{
		FuturableReturnType returnTypeAnnotation = realObject.getClass().getAnnotation(FuturableReturnType.class);
		HashingMethod method;

		if (returnTypeAnnotation == null)
		{
			method = getInterfaceHashingMethod(realObject);
			if (method == null)
			{
				method = FuturableConstants.DEFAULT_HASHING_METHOD;
			}
		} else
		{
			method = returnTypeAnnotation.hashingMethod();
			if (method == null)
			{
				method = FuturableConstants.DEFAULT_HASHING_METHOD;
			}
		}
		return method;
	}

	private HashingMethod getInterfaceHashingMethod(Object realObject)
	{
		HashingMethod method = null;
		Class<?>[] interfaces = realObject.getClass().getInterfaces();
		for (Class<?> interFace : interfaces)
		{
			FuturableReturnType returnType = interFace.getAnnotation(FuturableReturnType.class);
			if (returnType != null)
			{
				method = returnType.hashingMethod();
			} else
			{
				method = FuturableConstants.DEFAULT_HASHING_METHOD;
			}
		}
		return method;
	}
}
