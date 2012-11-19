package edu.buffalo.cse.cse605.project2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FuturableTestClass {

    private static final transient Logger LOG = LoggerFactory.getLogger(FuturableTestClass.class);


    @Futurable
    public List<String> getList() throws InterruptedException {
        return Arrays.asList("hello", "world");
    }

    @Futurable(executor = "userDefinedExecutor")
    public List<String> getOtherList() throws InterruptedException {
        return Arrays.asList("hello2", "world2");
    }


    @Futurable
    public List<String> wasteSomeTime(int i) {
        try {
            Thread.sleep(i * 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<String> returnValue = new ArrayList<String>();
        for (int j = 0; j < i; j++) {
            returnValue.add(Integer.toString(j));
        }

        LOG.debug("Done wasting {} time!", Integer.toString(i));

        return returnValue;
    }

    @FuturableQueue
    public Queue<List<String>> wasteALotOfTime(int count) {
        Queue<List<String>> returnedQueue = new LinkedList<List<String>>();


        for (int i = count; i > 0; i--) {
            returnedQueue.add(wasteSomeTime(i));
        }

        LOG.debug("Done wasting all of the time.");

        return returnedQueue;
    }
}
