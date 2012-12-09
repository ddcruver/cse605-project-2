package edu.buffalo.cse.cse605.project2;

import com.google.common.collect.Maps;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class PartialFuturableAspectProxyInvocationHandler implements InvocationHandler
{
	private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableAspect.class);

	private static final transient String HASH_SEPARATOR = ":";

	private final Object realObject;

	private boolean operationComplete = false;

	private final FuturableUtil utility;

	private final Map<String, AtomicBoolean> hasCompletedMap = Maps.newConcurrentMap();

	private final HashingMethod hashingMethod;

	public PartialFuturableAspectProxyInvocationHandler(Object originalObject, FuturableUtil futurableUtility)
	{
		realObject = originalObject;
		utility = futurableUtility;
		hashingMethod = utility.getHashingMethod(realObject);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{

		Object returnValue = null;
		// now we need to translate that to the real method call, on the hopefully completed real object
		String name = method.getName();
		Method methodObj = ReflectionUtils.findMethod(realObject.getClass(), name, method.getParameterTypes());

		// get the method argument we really want
		Object[] adjustedArgs = (args == null) ? new Object[0] : args;

		// Shortcut if the futurable has completed
		if (operationComplete)
		{
			returnValue = handleNormalMethod(realObject, method, methodObj, adjustedArgs);
			return returnValue;
		}

		PartialFuturableGetter getter = method.getAnnotation(PartialFuturableGetter.class);
		PartialFuturableSetter setter = method.getAnnotation(PartialFuturableSetter.class);
		PartialFuturableMarker marker = method.getAnnotation(PartialFuturableMarker.class);

		if (getter != null)
		{
			returnValue = handleGetter(realObject, method, methodObj, adjustedArgs);
		} else if (setter != null)
		{
			returnValue = handleSetter(realObject, method, methodObj, adjustedArgs);
		} else if (marker != null)
		{
			returnValue = handleMarker(realObject, method, methodObj, adjustedArgs);
		} else
		{
			returnValue = handleNormalMethod(realObject, method, methodObj, adjustedArgs);
		}

		return returnValue;
	}

	private Object handleNormalMethod(Object realObject2, Method method, Method methodObj, Object[] adjustedArgs)
	{
		LOG.trace("Executing normal method");
		return utility.executeMethod(realObject, methodObj, adjustedArgs);
	}

	private AtomicBoolean getCompletedState(String argumentHash)
	{
		// Give completed status to false if didn't already exist.
		return getCompletedState(argumentHash, Boolean.FALSE);
	}

	private AtomicBoolean getCompletedState(String argumentHash, Boolean completed)
	{

		AtomicBoolean hasCompleted = hasCompletedMap.get(argumentHash);
		if (hasCompleted == null)
		{
			hasCompleted = new AtomicBoolean(completed);
			hasCompletedMap.put(argumentHash, hasCompleted);
		}
		return hasCompleted;
	}

	/**
	 * @param method
	 * @param adjustedArgs
	 * @return
	 */

	private String getArgumentHash(Method method, Object[] adjustedArgs, boolean checkForFuturableValues)
	{
		StringBuilder hashBuilder = new StringBuilder();

		Annotation[][] parametersAnnotations = method.getParameterAnnotations();

		for (int p = 0; p < parametersAnnotations.length; p++)
		{

			boolean foundFuturableValueAnnotation = false;

			if (checkForFuturableValues)
			{
				foundFuturableValueAnnotation = checkForFuturableValueInParameter(parametersAnnotations, p);
			}

			if (!foundFuturableValueAnnotation)
			{

				if (hashingMethod == HashingMethod.HASH_CODE)
				{
					hashBuilder.append(adjustedArgs[p].hashCode());
				} else if (hashingMethod == HashingMethod.TO_STRING)
				{
					hashBuilder.append(adjustedArgs[p].toString());
				} else
				{
					throw new NotImplementedException("Get Argument does not yet support the HashingMethod '" + hashingMethod.getClass().getName() + "'");
				}
				hashBuilder.append(HASH_SEPARATOR);
			}
		}

		return hashBuilder.toString();
	}

	private boolean checkForFuturableValueInParameter(Annotation[][] parametersAnnotations, int p)
	{
		boolean foundAnnotation = false;
		Annotation[] parameterAnnotation = parametersAnnotations[p];

		for (Annotation annotation : parameterAnnotation)
		{
			LOG.trace("Annotation of {} parameter is {}", p, annotation);
			if (annotation.annotationType().equals(FuturableValue.class))
			{
				LOG.trace("Found {} Annotation", FuturableValue.class);
				foundAnnotation = true;
				break;
			}
		}
		return foundAnnotation;
	}

	protected Object handleSetter(Object realObject, Method method, Method methodObj, Object[] adjustedArgs)
	{
		String argumentHash = getArgumentHash(method, adjustedArgs, true);
		final AtomicBoolean hasCompleted;
		Object returnValue;

		hasCompleted = getCompletedState(argumentHash, Boolean.FALSE);

		synchronized (hasCompleted)
		{
			LOG.trace("Calling actual setter");
			returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
			LOG.trace("Returning from actual setter");
			hasCompleted.set(Boolean.TRUE);
			hasCompleted.notify();
		}

		return returnValue;
	}

	private Object handleMarker(Object realObject, Method method, Method methodObj, Object[] adjustedArgs)
	{

		String argumentHash = getArgumentHash(method, adjustedArgs, false);
		final AtomicBoolean hasCompleted;
		Object returnValue;

		hasCompleted = getCompletedState(argumentHash, Boolean.FALSE);

		synchronized (hasCompleted)
		{
			LOG.trace("Calling actual marker");
			returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
			LOG.trace("Returning from actual marker");
			hasCompleted.set(Boolean.TRUE);
			hasCompleted.notify();
		}

		return returnValue;
	}

	protected Object handleGetter(Object realObject, Method method, Method methodObj, Object[] adjustedArgs) throws InterruptedException
	{
		String argumentHash = getArgumentHash(method, adjustedArgs, false);
		final AtomicBoolean hasCompleted;
		Object returnValue;

		LOG.debug("Calling Getter with Argument Hash: {}", argumentHash);

		hasCompleted = getCompletedState(argumentHash);

		synchronized (hasCompleted)
		{
			while (!hasCompleted.get() && !operationComplete)
			{
				LOG.trace("Waiting on completion of value");
				hasCompleted.wait(1000);
				LOG.trace("Waking up while waiting on completion of value");
			}

			if (operationComplete)
			{
				LOG.trace("Waking up because previous operation was completed.");
			}

			returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
		}

		return returnValue;
	}

	public void setOperationComplete()
	{
		operationComplete = true;
	}
}
