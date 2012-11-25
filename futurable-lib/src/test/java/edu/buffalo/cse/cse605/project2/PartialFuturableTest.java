package edu.buffalo.cse.cse605.project2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/futurable-test-context.xml")
public class PartialFuturableTest {
   
	private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableTest.class);

    @Autowired
    private PartialFuturableTestClass futurable;
    
    @Test
    public void testDefaultExecutor() throws InterruptedException {
    	PartialFuturableTestReturnType type = new PartialFuturableTestReturnTypeImpl();
    	type = futurable.doPartialWork(type, null);
        LOG.debug("Return: {}", type);
        
        for(int x = 0; x < 10; x++)
        {
        	for(int y = 0; y < 10; y++)
        	{
        		if(LOG.isTraceEnabled())
        		{
        			StringBuilder builder = new StringBuilder();
        			builder.append(x);
        			builder.append(",");
        			builder.append(y);
        			LOG.trace("Value of {} is {}", builder.toString(), type.getValue(x, y));
        		}
        			
        	}
        }
        
        int expectedValue = 5;
        assertEquals("Unexpected value " + expectedValue + " for futurable set value", expectedValue, type.getValue(5, 5), 0.1);
    }
    
    @Test
    public void testUserDefinedExecutor() throws InterruptedException {
    	PartialFuturableTestReturnType type = new PartialFuturableTestReturnTypeImpl();
    	type = futurable.doPartialWorkWithUserDefinedExecutor(type, null);
        LOG.info("Return: {}", type);
        
        for(int x = 0; x < 10; x++)
        {
        	for(int y = 0; y < 10; y++)
        	{
        		if(LOG.isTraceEnabled())
        		{
        			StringBuilder builder = new StringBuilder();
        			builder.append(x);
        			builder.append(",");
        			builder.append(y);
        			LOG.trace("Value of {} is {}", builder.toString(), type.getValue(x, y));
        		}
        			
        	}
        }
        
        int expectedValue = 10;
        assertEquals("Unexpected value " + expectedValue + " for futurable set value", expectedValue, type.getValue(5, 5), 0.1);
    }

}
