package edu.buffalo.cse.cse605.project2.graphic.processing;

import org.slf4j.Logger;

public class ImageUtil
{

	public static int getRGB(int red, int green, int blue)
	{
		int rgb = red;
		rgb = (rgb << 8) + green;
		rgb = (rgb << 8) + blue;
		return rgb;
	}

	public static int getRed(int rgb)
	{
		return (rgb >> 16) & 0xFF;
	}

	public static int getGreen(int rgb)
	{
		return (rgb >> 8) & 0xFF;
	}

	public static int getBlue(int rgb)
	{
		return rgb & 0xFF;
	}

	public static void logColorComponents(Logger log, String message, int rgb)
	{

		if (log.isDebugEnabled())
		{
			int red = getRed(rgb);
			int green = getGreen(rgb);
			int blue = getBlue(rgb);

			logColorComponents(log, message, red, green, blue);
		}
	}

	public static void logColorComponents(Logger log, String message, int red, int green, int blue)
	{
		if (log.isDebugEnabled())
		{
			log.debug("{} {}", message, "Red: " + red + "; Green: " + green + "; Blue: " + blue);
		}
	}
}
