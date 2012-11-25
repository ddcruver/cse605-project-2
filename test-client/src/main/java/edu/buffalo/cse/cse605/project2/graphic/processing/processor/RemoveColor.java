package edu.buffalo.cse.cse605.project2.graphic.processing.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.buffalo.cse.cse605.project2.PartialFuturable;
import edu.buffalo.cse.cse605.project2.graphic.processing.Image;
import edu.buffalo.cse.cse605.project2.graphic.processing.ImageUtil;

public class RemoveColor
{
	private static final transient Logger LOG = LoggerFactory.getLogger(RemoveColor.class);
	
	@PartialFuturable
    public Image removeRed(Image in, Image out) throws InterruptedException {
    	
		int height = in.getHeight();
		int width = in.getWidth();
		
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				LOG.debug("Processing Red Remove {},{}", h, w);
				int originalPixel = in.getPixel(w, h);
				int red = ImageUtil.getRed(originalPixel);
				int green = ImageUtil.getGreen(originalPixel);
				int blue = ImageUtil.getBlue(originalPixel);
				red = 0;
				int newPixel = ImageUtil.getRGB(red, green, blue);
				out.setPixel(w, h, newPixel);
			}
		}
        return out;
    }
	
	@PartialFuturable
    public Image removeBlue(Image in, Image out) throws InterruptedException {
    	
		int height = in.getHeight();
		int width = in.getWidth();
		
		for(int h = 0; h < height; h++)
		{
			for(int w = 0; w < width; w++)
			{
				LOG.debug("Processing Blue Remove {},{}", h, w);
				int originalPixel = in.getPixel(w, h);
				int red = ImageUtil.getRed(originalPixel);
				int green = ImageUtil.getGreen(originalPixel);
				int blue = ImageUtil.getBlue(originalPixel);
				blue = 0;
				int newPixel = ImageUtil.getRGB(red, green, blue);
				out.setPixel(w, h, newPixel);
			}
		}
        return out;
    }
}
