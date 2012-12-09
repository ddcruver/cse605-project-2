package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.PartialFuturableCompleted;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;

public interface ImageProcessor extends PartialFuturableCompleted
{

	@PartialFuturable
	public <T> Image process(Image input, Image output, T argument) throws InterruptedException;
}
