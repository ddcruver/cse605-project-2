package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.SimplePartialFuturableCompleted;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;
import edu.buffalo.cse.cse605.project2.graphic.processing.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreyScale extends SimplePartialFuturableCompleted implements ImageProcessor
{
	private static final transient Logger LOG = LoggerFactory.getLogger(GreyScale.class);

	@PartialFuturable
	public Image process(Image in, Image out, Object unused) throws InterruptedException
	{
		int height = in.getHeight();
		int width = in.getWidth();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				LOG.debug("Processing Greyscale {},{}", x, y);
				int originalPixel = in.getPixel(x, y);
				int red = ImageUtil.getRed(originalPixel);
				int green = ImageUtil.getGreen(originalPixel);
				int blue = ImageUtil.getBlue(originalPixel);

				int max = Math.max(Math.max(red, green), blue);
				int min = Math.min(Math.min(red, green), blue);
				int grey = (max + min) / 2;
				int newPixel = ImageUtil.getRGB(grey, grey, grey);
				out.setPixel(x, y, newPixel);
			}
		}
		return out;
	}
}
