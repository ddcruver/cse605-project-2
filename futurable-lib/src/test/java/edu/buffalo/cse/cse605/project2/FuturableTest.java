package edu.buffalo.cse.cse605.project2;

import static org.junit.Assert.*;

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
public class FuturableTest {

	private static final transient Logger LOG = LoggerFactory.getLogger(FuturableTest.class);
	
	@Autowired
	private FuturableTestClass futurable;

	@Test
	public void testDefaultExecutor() throws InterruptedException {
		List<String> list = futurable.getList();
		list.subList(1,1);
		LOG.info("List Contents: {}", list);
		assertEquals(2, list.size());
	}
	
	@Test
	public void testUserDefinedExecutor() throws InterruptedException {
		List<String> list = futurable.getOtherList();
		list.subList(1,1);
		LOG.info("List Contents: {}", list);
		assertEquals(2, list.size());
	}
	
}
