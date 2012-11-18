import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountTest {

    public static final String WIKIPEDIA_FILE_LOCATION = "wikipedia_2006_corpus/";
    public static final String ALL_WIKIPEDIA_FILE_LOCATION = "all_wikipedia/";

    public static void main(String[] args) throws IOException {
        WordCountTest wct = new WordCountTest();
        wct.runNoQueue();
        wct.runWithQueue();
    }

    public void runWithQueue() throws IOException {
        File files[] = getWikipediaFiles();
        Queue<Map<String, AtomicInteger>> wordCounts = compute(files);
        int processed = 0;
        Map<String, AtomicInteger> finalMap = new HashMap<String, AtomicInteger>();
        System.out.println("Enter test to see what is finished on queue");
        if(! wordCounts.isEmpty() && processed <= files.length / 2 ){
                System.out.println("Merging a map in");
                merge(finalMap, wordCounts.remove());
        }
        wordCounts.clear();
        System.out.println("Completed processing at least half of the queue files.  Finished with size: " + finalMap.size());
    }

    //@FuturableQueue
    private Queue compute(File[] files) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");
        WordCountGenerator wordCountGenerator = context.getBean("wordCountGenerator", WordCountGenerator.class);
        Queue<Map<String, AtomicInteger>> wordCounts = new LinkedList<Map<String, AtomicInteger>>();
        for(File file: files){
            wordCounts.add(wordCountGenerator.generateWordCount(file));
            System.out.println("Sent one out ... " + file.getName());
        }
        return wordCounts;
    }


    public void runNoQueue() throws IOException {
        File files[] = getWikipediaFiles();
        run(files);
    }

    private void run(File[] files) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");
        WordCountGenerator wordCountGenerator = context.getBean("wordCountGenerator", WordCountGenerator.class);
        Queue<Map<String, AtomicInteger>> wordCounts = new LinkedList<Map<String, AtomicInteger>>();

        for(File file: files){
            wordCounts.add(wordCountGenerator.generateWordCount(file));
            System.out.println("Sent one out ... " + file.getName());
        }
        System.out.println("Finished sending out all");
        Map<String, AtomicInteger> finalMap = new HashMap<String, AtomicInteger>();

        while(!wordCounts.isEmpty()) {
            System.out.println("Merging a map in");
            merge(finalMap, wordCounts.remove());
        }
        System.out.println("Finished all!! Final map size :" + finalMap.size());
        ((ConfigurableApplicationContext) context).close();

    }

    public File[] getWikipediaFiles() throws IOException {
        Resource resourceOne = new ClassPathResource(ALL_WIKIPEDIA_FILE_LOCATION);
        resourceOne.getFile().listFiles();
        return resourceOne.getFile().listFiles();
    }

    private void merge(Map<String, AtomicInteger> a, Map<String, AtomicInteger> b) {
        for (Map.Entry<String, AtomicInteger> entry : b.entrySet()) {
            AtomicInteger atomicInteger = a.get(entry.getKey());

            if(atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
                a.put(entry.getKey(), atomicInteger);
            }

            atomicInteger.addAndGet(entry.getValue().get());
        }
    }

    public int getTokenCount(){
        return 0;
    }

}
