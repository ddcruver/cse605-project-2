package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DegradeFilter
{
	private static final transient Logger LOG = LoggerFactory.getLogger(GreyScale.class);

	@PartialFuturable
	public Image process(Image in, Image out) throws InterruptedException
	{
		int height = in.getHeight();
		int width = in.getWidth();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				LOG.debug("Copying {},{}", x, y);
				int originalPixel = in.getPixel(x, y);
				originalPixel = originalPixel & 0x00ABABAB;
				out.setPixel(x, y, originalPixel);
			}
		}
		return out;
	}
}
