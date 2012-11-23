package edu.buffalo.cse.cse605.project2;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
    	PartialFuturableReturnType type = new PartialFuturableReturnTypeImpl();
    	type = futurable.doPartialWork(type);
        LOG.info("Return: {}", type);
        
        double value = type.getValue(1, 1);
        LOG.info("Value of 1,1 is {}", value);
        
    }
    
    @Test
    public void testUserDefinedExecutor() throws InterruptedException {
    	PartialFuturableReturnType type = new PartialFuturableReturnTypeImpl();
    	type = futurable.doPartialWorkWithUserDefinedExecutor(type);
        LOG.info("Return: {}", type);
        
        double value = type.getValue(1, 1);
        LOG.info("Value of 1,1 is {}", value);
    }

}
