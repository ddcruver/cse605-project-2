package edu.buffalo.cse.cse605.project2;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class PartialFuturableAspectProxyInvocationHandler implements InvocationHandler
{
	private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableAspect.class);

	private static final transient String HASH_SEPERATOR = ":";
	
	private final Object realObject;
	
	private final FuturableUtil utility;
	
	private final Map<String, AtomicBoolean> hasCompletedMap = new HashMap<String, AtomicBoolean>();

	private final HashingMethod hashingMethod;
	
	public PartialFuturableAspectProxyInvocationHandler(Object originalObject, FuturableUtil futurableUtility)
	{
		realObject = originalObject;
		utility = futurableUtility;
		hashingMethod = utility.getHashingMethod(realObject);
	}
	
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
        	returnValue = handleGetter(realObject, method, methodObj, adjustedArgs);
        }
        else if(setter != null)
        {
        	returnValue = handleSetter(realObject, method, methodObj, adjustedArgs);
        }
        else
        {
        	LOG.debug("Executing normal method");
        	returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
        }
        
        return returnValue;            
    }
   
    private AtomicBoolean getCompletedState(String argumentHash) {
    	// Give completed status to false if didn't already exist.
    	return getCompletedState(argumentHash, Boolean.FALSE);
    }
    
    private AtomicBoolean getCompletedState(String argumentHash, Boolean completed) {
		
    	AtomicBoolean hasCompleted = hasCompletedMap.get(argumentHash);
		if(hasCompleted == null)
		{
			hasCompleted = new AtomicBoolean(completed);
			hasCompletedMap.put(argumentHash, hasCompleted);
		}
		return hasCompleted;
	}

    /**
     * 
     * This function currently hashes all the arguments but ideally should only hash those not annotated with 
     * {@link FuturableValue}.
     * @param method 
     * 
     * @param adjustedArgs
     * @return
     */
    
	private String getArugumentHash(Method method, Object[] adjustedArgs) {
		StringBuilder hashBuilder = new StringBuilder();
		
		Annotation[][] paramatersAnnotations = method.getParameterAnnotations();
		
		for(int p = 0; p < paramatersAnnotations.length; p++)
		{
			Annotation[] parameterAnnotation = paramatersAnnotations[p];
			boolean foundFuturableValueAnnotation = false;
			for(Annotation annot : parameterAnnotation)
			{
				LOG.debug("Annotation of {} parameter is {}", p, annot);
				if(annot.annotationType().equals(FuturableValue.class))
				{
					LOG.debug("Found {} Annotation", FuturableValue.class);
					foundFuturableValueAnnotation = true;
				}
			}
			
			if(foundFuturableValueAnnotation == false)
			{
				
				if(hashingMethod == HashingMethod.HASH_CODE)
				{
					hashBuilder.append(adjustedArgs[p].hashCode());
				} else if(hashingMethod == HashingMethod.TO_STRING)
				{
					hashBuilder.append(adjustedArgs[p].toString());
				} else
				{
					throw new NotImplementedException("Get Argument does not yet support the HashingMethod '" + hashingMethod.getClass().getName() + "'");
				}
				hashBuilder.append(HASH_SEPERATOR);
			}
		}
		
		return hashBuilder.toString();
	}

	protected Object handleSetter(Object realObject, Method method, Method methodObj, Object[] adjustedArgs)
    {
		String argumentHash = getArugumentHash(method, adjustedArgs);
		final AtomicBoolean hasCompleted;
		Object returnValue;
		
		LOG.debug("Original call to Setter with Argument Hash: {}", argumentHash);

    	synchronized (hasCompletedMap) {
    		hasCompleted = getCompletedState(argumentHash, Boolean.FALSE);
    	}
    	
    	synchronized (hasCompleted) {
    		LOG.debug("Calling actual setter");
    		returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
    		LOG.debug("Returning from actual setter");
    		hasCompleted.set(Boolean.TRUE);
    		hasCompleted.notify();
		}
    	
		return returnValue;
	}

	protected Object handleGetter(Object realObject, Method method, Method methodObj, Object[] adjustedArgs) throws InterruptedException
	{
		String argumentHash = getArugumentHash(method, adjustedArgs);
		final AtomicBoolean hasCompleted;
        Object returnValue;
        
		LOG.debug("Calling Getter with Argument Hash: {}", argumentHash);
    	
		synchronized (hasCompletedMap) {
        	hasCompleted = getCompletedState(argumentHash);					
        }
    	
    	synchronized (hasCompleted)
    	{
    		while(hasCompleted.get() == Boolean.FALSE.booleanValue())
    		{
    			LOG.debug("Waiting on completion of value");
    			hasCompleted.wait();
    			LOG.debug("Waking up while waiting on completion of value");
    		}
    		returnValue = utility.executeMethod(realObject, methodObj, adjustedArgs);
		}
		
		return returnValue;
	}
}
