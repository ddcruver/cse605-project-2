package edu.buffalo.cse.cse605.project2.graphic.processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageLoader
{
	private static final transient Logger LOG = LoggerFactory.getLogger(ImageLoader.class);
	
	public Image loadImage(InputStream imageStream) throws IOException
	{
		BufferedImage bufferedImage = ImageIO.read(imageStream);
		ImageImpl image = new ImageImpl();
		image.setImage(bufferedImage);
		return image;
	}
	
	public void saveImage(Image image, File file) throws IOException
	{
		LOG.debug("Image Instance: {}", image.getClass());
		ImageIO.write(image.getBufferedImage(), "png", file);
	}

}
