package edu.buffalo.cse.cse605.project2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

public class PartialFuturableInvocationHandler implements InvocationHandler
{
	private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableAspect.class);

	private static final transient String HASH_SEPERATOR = ":";
	
	private Object realObject;
	
	private FuturableUtil utility;
	
	
	private  Map<String, AtomicBoolean> hasCompletedMap = new HashMap<String, AtomicBoolean>();

	
	public PartialFuturableInvocationHandler(Object originalObject, FuturableUtil futurableUtility)
	{
		realObject = originalObject;
		utility = futurableUtility;
	}
	
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOG.debug("Giving to real object");

        // now we need to translate that to the real method call, on the hopefully completed real object
        String name = method.getName();
        PartialFuturableGetter getter = method.getAnnotation(PartialFuturableGetter.class);
        PartialFuturableSetter setter = method.getAnnotation(PartialFuturableSetter.class);
        
        Object[] adjustedArgs = (args == null) ? new Object[0] : args;
        
        String argumentHash = getArugumentHash(adjustedArgs);
        
        
        // get the method argument we really want
        Method methodObj = ReflectionUtils.findMethod(realObject.getClass(), name, method.getParameterTypes());

        Object returnValue;
        
        final AtomicBoolean hasCompleted;
        
        if(getter != null)
        {
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
        			LOG.debug("Waking up.");
        		}
        		returnValue = handleGetter(realObject, method, methodObj, adjustedArgs);
			}
        	
        }
        else if(setter != null)
        {
        	LOG.debug("Original call to Setter with Argument Hash: {}", argumentHash);
        	// TODO this is a hack we should serialize the value of a setter into hash anyways
        	int lastArgumentHash = argumentHash.substring(0, argumentHash.length()-1).lastIndexOf(HASH_SEPERATOR);
        	
        	LOG.debug("Arugment Hash Last: {}", lastArgumentHash);
        	argumentHash = argumentHash.substring(0, lastArgumentHash+1);
        	
        	LOG.debug("Calling Setter with Argument Hash: {}", argumentHash);
        	synchronized (hasCompletedMap) {
        		hasCompleted = getCompletedState(argumentHash, Boolean.FALSE);
        	}
        	
        	synchronized (hasCompleted) {
        		LOG.debug("Calling actual setter");
        		returnValue = handleSetter(realObject, method, methodObj, adjustedArgs);
        		LOG.debug("Returning from actual setter");
        		hasCompleted.set(Boolean.TRUE);
        		hasCompleted.notify();
			}
        	
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

	private String getArugumentHash(Object[] adjustedArgs) {
		StringBuilder hashBuilder = new StringBuilder();
		
		for(Object argument : adjustedArgs)
		{
			hashBuilder.append(argument.toString());
			hashBuilder.append(HASH_SEPERATOR );
		}
		
		return hashBuilder.toString();
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
}
