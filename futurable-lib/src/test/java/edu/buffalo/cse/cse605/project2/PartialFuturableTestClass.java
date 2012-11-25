package edu.buffalo.cse.cse605.project2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialFuturableTestClass {

    private static final transient Logger LOG = LoggerFactory.getLogger(PartialFuturableTestClass.class);

    @PartialFuturable
    public PartialFuturableTestReturnType doPartialWork(PartialFuturableTestReturnType input, PartialFuturableTestReturnType proxiedOutput) throws InterruptedException {
    	proxiedOutput.setValue(1, 1, 5);
        return proxiedOutput;
    }

    @PartialFuturable(executor = "userDefinedExecutor")
    public PartialFuturableTestReturnType doPartialWorkWithUserDefinedExecutor(PartialFuturableTestReturnType input, PartialFuturableTestReturnType proxiedOutput) throws InterruptedException {
    	proxiedOutput.setValue(1, 1, 10);
        return proxiedOutput;
    }
}
