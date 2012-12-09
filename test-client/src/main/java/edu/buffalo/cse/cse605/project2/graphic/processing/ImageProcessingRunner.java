package edu.buffalo.cse.cse605.project2.graphic.processing;


import edu.buffalo.cse.cse605.project2.graphic.processing.processor.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class ImageProcessingRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(ImageProcessingRunner.class);

	private static final String SPRING_CONTEXT_PATH = "graphic/image-processing-context.xml";

	public static void main(String[] args) throws IOException, InterruptedException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_PATH);

		Resource imageResource = new ClassPathResource("/graphic/apple.jpg");

		ImageLoader loader = new ImageLoader();
		Image image = loader.loadImage(imageResource.getInputStream());

		BlurImage blurImageFilter = context.getBean(BlurImage.class);
		RemoveColor removeColorFilter = context.getBean(RemoveColor.class);
		ImageProcessor greyScaleFilter = context.getBean("greyScaleFilter", ImageProcessor.class);
		ImageProcessor noOpFilter = context.getBean("noOpFilter", ImageProcessor.class);
		DelayFilter delayFilter = context.getBean(DelayFilter.class);
		DegradeFilter degradeFilter = context.getBean(DegradeFilter.class);

		LOG.debug("Degrading Image");
		Image degradeImage = degradeFilter.process(image, null);

		//Image delayImage = delayFilter.delay(degradeImage, null, 100);
		Image bluredImage = blurImageFilter.blurImage(degradeImage, null, 5);
		Image delayImage2 = delayFilter.delay(degradeImage, null, 100);
		Image greyScaleImage = greyScaleFilter.process(delayImage2, null, null);

		// Causes exception to be thrown
		//Image bluredImageOut = new ImageImpl();
		//Image bluredImage = blurImageFilter.blurImage(noOpImage, bluredImageOut, 5);

		int snapshot = 0;
		File outputFolder = new File("target/images/");
		FileUtils.deleteQuietly(outputFolder);
		Thread.sleep(2000);
		outputFolder = new File("target/images/");
		outputFolder.mkdirs();

		boolean done = false;

		do
		{
			done = greyScaleFilter.getComplete();
			snapshot++;
			File outputFile = new File(outputFolder, "apple-new-" + snapshot + ".png");
			LOG.debug("OutFile: {}", outputFile.getAbsolutePath());
			loader.saveImage(greyScaleImage, outputFile);
			Thread.sleep(2000);
		} while (!done);

		((ConfigurableApplicationContext) context).close();
	}
}
