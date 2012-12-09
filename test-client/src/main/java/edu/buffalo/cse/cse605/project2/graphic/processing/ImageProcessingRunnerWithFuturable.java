package edu.buffalo.cse.cse605.project2.graphic.processing;


import edu.buffalo.cse.cse605.project2.graphic.processing.processor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public class ImageProcessingRunnerWithFuturable
{
	private static final transient Logger LOG = LoggerFactory.getLogger(ImageProcessingRunnerWithFuturable.class);

	private static final String SPRING_CONTEXT_PATH = "graphic/image-processing-context.xml";

	public static void main(String[] args) throws IOException, InterruptedException
	{
		LOG.info("Starting");
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

		LOG.info("Degrading Image");
		Image degradeImage = degradeFilter.process(image, null);
		//LOG.info("Delaying Image");
		//Image delayImage = delayFilter.delay(degradeImage, null, 100);
		LOG.info("Blurring Image");
		Image blurredImage = blurImageFilter.blurImage(degradeImage, null, 5);
		//LOG.info("Delaying Image");
		//Image delayImage2 = delayFilter.delay(degradeImage, null, 100);
		LOG.info("Grey Scaling Image");
		Image greyScaleImage = greyScaleFilter.process(blurredImage, null, null);

		File outputFolder = new File("target/images1/");
		outputFolder = new File("target/images1/");
		outputFolder.mkdirs();

		boolean done = false;

		do
		{
			done = greyScaleFilter.getComplete();
		} while (!done);

		LOG.info("Done Transforming Image");

		File outputFile = new File(outputFolder, "apple-new.png");
		LOG.info("OutFile: {}", outputFile.getAbsolutePath());
		loader.saveImage(greyScaleImage, outputFile);
		LOG.info("Done");

		((ConfigurableApplicationContext) context).close();
	}
}
