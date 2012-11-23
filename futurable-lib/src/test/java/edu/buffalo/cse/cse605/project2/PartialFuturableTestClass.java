package edu.buffalo.cse.cse605.project2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialFuturableTestClass {

    private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableTestClass.class);

    @PartialFuturable
    public PartialFuturableReturnType doPartialWork(PartialFuturableReturnType type) throws InterruptedException {
    	type.setValue(1, 1, 5);
        return type;
    }

    @PartialFuturable(executor = "userDefinedExecutor")
    public PartialFuturableReturnType doPartialWorkWithUserDefinedExecutor(PartialFuturableReturnType type) throws InterruptedException {
    	type.setValue(1, 1, 10);
        return type;
    }
}
