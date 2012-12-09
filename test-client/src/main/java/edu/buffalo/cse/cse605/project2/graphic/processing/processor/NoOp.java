package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.SimplePartialFuturableCompleted;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOp extends SimplePartialFuturableCompleted implements ImageProcessor
{
	private static final transient Logger LOG = LoggerFactory.getLogger(GreyScale.class);

	@SuppressWarnings("hiding")
	@Override
	@PartialFuturable
	public <Integer> Image process(Image input, Image output, Integer argument) throws InterruptedException
	{
		LOG.debug("Did nothing");
		return output;
	}
}
