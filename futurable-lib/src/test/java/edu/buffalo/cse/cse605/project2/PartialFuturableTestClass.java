package edu.buffalo.cse.cse605.project2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialFuturableTestClass {

    private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableTestClass.class);

    @PartialFuturable
    public PartialFuturableTestReturnType doPartialWork(PartialFuturableTestReturnType input, PartialFuturableTestReturnType output) throws InterruptedException {
    	for(int x = 0; x < 10; x++)
    	{
    		for(int y = 0; y < 10; y++)
    		{
    			if(x == 5 && y == 5)
    			{
    				output.setValue(5, 5, 5);
    			} else
    			{
    				output.markValue(x, y);
    			}
    		}
    		
    	}
        return output;
    }

    @PartialFuturable(executor = "userDefinedExecutor")
    public PartialFuturableTestReturnType doPartialWorkWithUserDefinedExecutor(PartialFuturableTestReturnType input, PartialFuturableTestReturnType output) throws InterruptedException {
    	for(int x = 0; x < 10; x++)
    	{
    		for(int y = 0; y < 10; y++)
    		{
    			if(x == 5 && y == 5)
    			{
    				output.setValue(5, 5, 10);
    			} else
    			{
    				output.markValue(x, y);
    			}
    		}
    		
    	}
        return output;
    }
}
