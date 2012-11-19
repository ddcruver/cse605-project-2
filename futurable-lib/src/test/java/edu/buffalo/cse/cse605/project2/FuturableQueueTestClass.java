package edu.buffalo.cse.cse605.project2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FuturableQueueTestClass {
    private static final transient Logger LOG = LoggerFactory.getLogger(FuturableQueueTestClass.class);


    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private FuturableTestClass futurableTestClass;


    @FuturableQueue
    public Queue<List<String>> wasteALotOfTime(int count) {
        return wasteALotOfTimeNormally(count);
    }

    public Queue<List<String>> wasteALotOfTimeNormally(int count) {
        Queue<List<String>> returnedQueue = new LinkedList<List<String>>();

        for (int i = count; i > 0; i--) {
            returnedQueue.add(futurableTestClass.wasteSomeTime(i));
        }

        LOG.debug("Done wasting all of the time.");

        return returnedQueue;
    }
}
