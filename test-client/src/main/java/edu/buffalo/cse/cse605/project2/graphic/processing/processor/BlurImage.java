package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;
import edu.buffalo.cse.cse605.project2.graphic.processing.ImageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlurImage
{
	private static final transient Logger LOG = LoggerFactory.getLogger(BlurImage.class);

	@PartialFuturable
	public Image blurImage(Image in, Image out, int blurRadius) throws InterruptedException
	{

		int height = in.getHeight();
		int width = in.getWidth();

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				LOG.debug("Processing Blur {},{}", x, y);
				double redTotal = 0;
				double greenTotal = 0;
				double blueTotal = 0;

				int originalPixel = in.getPixel(x, y);
				int totalPixels = 0;
				ImageUtil.logColorComponents(LOG, "Original Pixel:", originalPixel);

				for (int ky = -blurRadius; ky <= blurRadius; ++ky)
				{
					for (int kx = -blurRadius; kx <= blurRadius; ++kx)
					{
						int inX = x + kx;
						int inY = y + ky;
						if (inX < 0 || inX >= width || inY < 0 || inY >= height)
						{

						} else
						{
							int original = in.getPixel(inX, inY);
							redTotal += ImageUtil.getRed(original);
							greenTotal += ImageUtil.getGreen(original);
							blueTotal += ImageUtil.getBlue(original);
							totalPixels++;
						}
					}
				}
				LOG.debug("Pixels Processed: {}", totalPixels);
				int redNew = (int) (redTotal / totalPixels);
				int greenNew = (int) (greenTotal / totalPixels);
				int blueNew = (int) (blueTotal / totalPixels);

				int newPixel = ImageUtil.getRGB(redNew, greenNew, blueNew);
				ImageUtil.logColorComponents(LOG, "New Pixel:", newPixel);
				out.setPixel(x, y, newPixel);
			}
		}
		return out;
	}
}
