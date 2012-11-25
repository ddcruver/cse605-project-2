package edu.buffalo.cse.cse605.project2.graphic.processing;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import edu.buffalo.cse.cse605.project2.graphic.processing.processor.BlurImage;
import edu.buffalo.cse.cse605.project2.graphic.processing.processor.GreyScale;
import edu.buffalo.cse.cse605.project2.graphic.processing.processor.NoOp;
import edu.buffalo.cse.cse605.project2.graphic.processing.processor.RemoveColor;

public class ImageProcessingRunner
{
	private static final transient Logger LOG = LoggerFactory.getLogger(ImageProcessingRunner.class);
	
	private static final String SPRING_CONTEXT_PATH = "graphic/image-processing-context.xml";
	
	public static void main(String[] args) throws IOException, InterruptedException {
		ApplicationContext context = new ClassPathXmlApplicationContext(SPRING_CONTEXT_PATH);
		
		Resource imageResource = new ClassPathResource("/graphic/apple.jpg");
		
		ImageLoader loader = new ImageLoader();
		Image image = loader.loadImage(imageResource.getInputStream());
		
		BlurImage blurImageFilter = context.getBean(BlurImage.class);
		RemoveColor removeColorFilter = context.getBean(RemoveColor.class);
		GreyScale greyScaleFilter = context.getBean(GreyScale.class);
		NoOp noOpFilter = context.getBean(NoOp.class);
		
		Image bluredImage = blurImageFilter.blurImage(image, null, 5);
		Image afterImage = greyScaleFilter.lighness(bluredImage, null);
		//removeColorFilter.removeBlue(afterImage, null);
		
		int snapshot = 0;
		File outputFolder = new File("images/");
		FileUtils.deleteQuietly(outputFolder);
		Thread.sleep(2000);
		outputFolder = new File("images/");
		outputFolder.mkdir();
		
		while(true)
		{
			snapshot++;
			File outputFile = new File(outputFolder, "apple-new-" + snapshot + ".png");
			LOG.debug("OutFile: {}", outputFile.getAbsolutePath());
			loader.saveImage(afterImage, outputFile);
		
			Thread.sleep(2000);
		}
		
	}
	
	
}
