package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelayFilter
{
	private static final transient Logger LOG = LoggerFactory.getLogger(DelayFilter.class);

	@PartialFuturable
	public Image delay(Image in, Image out, int delay) throws InterruptedException
	{
		int height = in.getHeight();
		int width = in.getWidth();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				LOG.debug("Copying {},{}", x, y);
				int originalPixel = in.getPixel(x, y);
				out.setPixel(x, y, originalPixel);
			}
			Thread.sleep(delay);
		}
		return out;
	}
}
