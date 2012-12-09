package edu.buffalo.cse.cse605.project2.graphic.processing;


import edu.buffalo.cse.cse605.project2.graphic.processing.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class ImageProcessingRunnerWithoutFuturable
{
	private static final transient Logger LOG = LoggerFactory.getLogger(ImageProcessingRunnerWithoutFuturable.class);

	public static void main(String[] args) throws IOException, InterruptedException
	{
		LOG.info("Starting");
		Resource imageResource = new ClassPathResource("/graphic/apple.jpg");

		ImageLoader loader = new ImageLoader();
		Image image = loader.loadImage(imageResource.getInputStream());

		BlurImage blurImageFilter = new BlurImage();
		RemoveColor removeColorFilter = new RemoveColor();
		ImageProcessor greyScaleFilter = new GreyScale();
		ImageProcessor noOpFilter = new NoOp();
		DelayFilter delayFilter = new DelayFilter();
		DegradeFilter degradeFilter = new DegradeFilter();

		LOG.info("Degrading Image");
		Image degradeImage = degradeFilter.process(image, new ImageImpl(image));
		//LOG.info("Delaying Image");
		//Image delayImage = delayFilter.delay(degradeImage, new ImageImpl(degradeImage), 100);
		LOG.info("Blurring Image");
		Image blurredImage = blurImageFilter.blurImage(degradeImage, new ImageImpl(degradeImage), 5);
		//LOG.info("Delaying Image");
		//Image delayImage2 = delayFilter.delay(degradeImage, new ImageImpl(blurredImage), 100);
		LOG.info("Grey Scaling Image");
		Image greyScaleImage = greyScaleFilter.process(blurredImage, new ImageImpl(blurredImage), null);

		File outputFolder = new File("target/images2/");
		outputFolder = new File("target/images2/");
		outputFolder.mkdirs();

		LOG.info("Done Transforming Image");
		File outputFile = new File(outputFolder, "apple-new.png");
		LOG.info("OutFile: {}", outputFile.getAbsolutePath());
		loader.saveImage(greyScaleImage, outputFile);
		LOG.info("Done");
	}
}
