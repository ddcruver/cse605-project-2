import edu.buffalo.cse.cse605.project2.FuturableQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountTest
{
	private static final transient Logger LOG = LoggerFactory.getLogger(WordCountTest.class);
	public static final String WIKIPEDIA_FILE_LOCATION = "wikipedia_2006_corpus/";
	public static final String ALL_WIKIPEDIA_FILE_LOCATION = "all_wikipedia/";
	private File files[];


	public static void main(String[] args) throws IOException
	{
		WordCountTest wct = new WordCountTest();
		wct.singleThreadTiming();
		wct.standardThreadPoolTiming();
		wct.futurableTiming();

		//wct.runWithQueue();
	}

	public WordCountTest() throws IOException
	{
		files = getWikipediaFiles();
	}

	private void singleThreadTiming() throws IOException
	{
		Queue<Map<String, AtomicInteger>> wordCounts = new LinkedList<>();
		OpenNlpProcessor textProcessor = new OpenNlpProcessor();
		WordCountGenerator wordCountGenerator = new WordCountGenerator();
		
		LOG.info("Begin file processing.");

		for (File file : files)
		{
			textProcessor.processDocument(file);
			//wordCounts.add(wordCountGenerator.generateWordCount(file));
			LOG.info("Processed file");
		}

		LOG.info("End file processing.");
	}

	private void standardThreadPoolTiming() throws IOException
	{
		final ExecutorService tpe = Executors.newFixedThreadPool(128);
		final Queue<Map<String, AtomicInteger>> wordCounts = new LinkedList<>();
		final OpenNlpProcessor tokenGenerator = new OpenNlpProcessor();
		
		final WordCountGenerator wordCountGenerator = new WordCountGenerator();
		final File[] files = getWikipediaFiles();
		LOG.info("Begin file processing.");

		for (int i = 0; i < files.length; i++)
		{
			final int count = i;
			tpe.submit(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						tokenGenerator.processDocument(files[count]);
						//wordCounts.add(wordCountGenerator.generateWordCount(files[count]));
						LOG.info("Processed file");
					} catch (IOException e)
					{
						LOG.error(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
					}
				}
			});
		}
		LOG.info("End file precessing.");
	}

	private void futurableTiming() throws IOException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");
		WordCountGenerator wordCountGenerator = context.getBean("wordCountGenerator", WordCountGenerator.class);
		OpenNlpProcessor textProcessor = context.getBean("textProcessor", OpenNlpProcessor.class);
		
		Queue<Map<String, AtomicInteger>> wordCounts = new LinkedList<>();

		LOG.info("Begin file processing");

		for (File file : files)
		{
			textProcessor.processDocument(file);
			//wordCounts.add(wordCountGenerator.generateWordCount(file));
			LOG.info("Processed file");
		}
		LOG.info("End file processing");
		Map<String, AtomicInteger> finalMap = new HashMap<>();

		while (!wordCounts.isEmpty())
		{
			System.out.println("Merging a map in");
			merge(finalMap, wordCounts.remove());
		}
		System.out.println("Finished all!! Final map size :" + finalMap.size());
		((ConfigurableApplicationContext) context).close();
	}

	public void runWithQueue() throws IOException
	{
		Queue<Map<String, AtomicInteger>> wordCounts = compute();
		int processed = 0;
		Map<String, AtomicInteger> finalMap = new HashMap<>();
		System.out.println("Enter test to see what is finished on queue");
		if (!wordCounts.isEmpty() && processed <= files.length / 2)
		{
			System.out.println("Merging a map in");
			merge(finalMap, wordCounts.remove());
		}
		wordCounts.clear();
		System.out.println("Completed processing at least half of the queue files.  Finished with size: " + finalMap.size());
	}

	@FuturableQueue
	private Queue<Map<String, AtomicInteger>> compute() throws IOException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");
		WordCountGenerator wordCountGenerator = context.getBean("wordCountGenerator", WordCountGenerator.class);
		Queue<Map<String, AtomicInteger>> wordCounts = new LinkedList<>();
		for (File file : files)
		{
			wordCounts.add(wordCountGenerator.generateWordCount(file));
			System.out.println("Sent one out ... " + file.getName());
		}
		return wordCounts;
	}


	public File[] getWikipediaFiles() throws IOException
	{
		Resource resourceOne = new ClassPathResource(ALL_WIKIPEDIA_FILE_LOCATION);
		File[] files = resourceOne.getFile().listFiles();

		assert files != null;

		Arrays.sort(files);

		return files;
	}

	private void merge(Map<String, AtomicInteger> a, Map<String, AtomicInteger> b)
	{
		for (Map.Entry<String, AtomicInteger> entry : b.entrySet())
		{
			AtomicInteger atomicInteger = a.get(entry.getKey());

			if (atomicInteger == null)
			{
				atomicInteger = new AtomicInteger(0);
				a.put(entry.getKey(), atomicInteger);
			}

			atomicInteger.addAndGet(entry.getValue().get());
		}
	}
}
