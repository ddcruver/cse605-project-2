package edu.buffalo.cse.cse605.project2.graphic.processing;

import java.awt.image.BufferedImage;

import edu.buffalo.cse.cse605.project2.FuturableReturnType;
import edu.buffalo.cse.cse605.project2.FuturableValue;
import edu.buffalo.cse.cse605.project2.HashingMethod;
import edu.buffalo.cse.cse605.project2.PartialFuturableGetter;
import edu.buffalo.cse.cse605.project2.PartialFuturableSetter;

@FuturableReturnType(hashingMethod = HashingMethod.TO_STRING)
public interface Image {
	
	@PartialFuturableGetter
	public int getPixel(int x, int y);

	@PartialFuturableSetter
	public void setPixel(int x, int y, @FuturableValue int rgb);
	
	public int getHeight();
	
	public int getWidth();
	
	public BufferedImage getBufferedImage();
	
}
