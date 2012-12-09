package edu.buffalo.cse.cse605.project2.graphic.processing;

import edu.buffalo.cse.cse605.project2.*;

import java.awt.image.BufferedImage;

@FuturableReturnType(hashingMethod = HashingMethod.TO_STRING)
public class ImageImpl implements Image
{

	private int height = 0;
	private int width = 0;
	private BufferedImage bImage;

	public ImageImpl()
	{
	}

	public ImageImpl(Image image)
	{
		bImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
	}

	@Override
	@PartialFuturableGetter
	public int getPixel(int x, int y)
	{
		return bImage.getRGB(x, y);
	}

	@Override
	@PartialFuturableSetter
	public void setPixel(int x, int y, @FuturableValue int rgb)
	{
		bImage.setRGB(x, y, rgb);
	}

	public void setImage(BufferedImage image)
	{
		bImage = image;
	}

	@Override
	public int getHeight()
	{
		return bImage.getHeight();
	}

	@Override
	public int getWidth()
	{
		return bImage.getWidth();
	}

	public BufferedImage getBufferedImage()
	{
		return bImage;
	}
}
