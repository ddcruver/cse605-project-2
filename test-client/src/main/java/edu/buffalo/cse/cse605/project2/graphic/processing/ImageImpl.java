package edu.buffalo.cse.cse605.project2.graphic.processing;

import java.awt.image.BufferedImage;

import edu.buffalo.cse.cse605.project2.FuturableReturnType;
import edu.buffalo.cse.cse605.project2.FuturableValue;
import edu.buffalo.cse.cse605.project2.HashingMethod;
import edu.buffalo.cse.cse605.project2.PartialFuturableGetter;
import edu.buffalo.cse.cse605.project2.PartialFuturableSetter;

@FuturableReturnType(hashingMethod=HashingMethod.TO_STRING)
public class ImageImpl implements Image {

	private int height = 0;
	private int width = 0;
	private BufferedImage bImage;
	
	@Override
	@PartialFuturableGetter
	public int getPixel(int x, int y) {
		return bImage.getRGB(x, y);
	}

	@Override
	@PartialFuturableSetter
	public void setPixel(int x, int y, @FuturableValue int rgb) {
		bImage.setRGB(x, y, rgb);
	}

	public void setImage(BufferedImage image)
	{
		bImage = image;
	}

	@Override
	public int getHeight() {
		return bImage.getHeight();
	}

	@Override
	public int getWidth() {
		return bImage.getWidth();
	}

	public BufferedImage getBufferedImage() {
		return bImage;
	}

}
