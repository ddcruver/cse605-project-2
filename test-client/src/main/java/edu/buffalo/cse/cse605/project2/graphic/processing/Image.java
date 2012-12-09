package edu.buffalo.cse.cse605.project2.graphic.processing;

import edu.buffalo.cse.cse605.project2.*;

import java.awt.image.BufferedImage;

@FuturableReturnType(hashingMethod = HashingMethod.TO_STRING)
public interface Image
{

	@PartialFuturableGetter
	public int getPixel(int x, int y);

	@PartialFuturableSetter
	public void setPixel(int x, int y, @FuturableValue int rgb);

	public int getHeight();

	public int getWidth();

	public BufferedImage getBufferedImage();
}
