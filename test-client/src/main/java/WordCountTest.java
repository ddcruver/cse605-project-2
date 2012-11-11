import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountTest {

    public static final String WIKIPEDIA_FILE_LOCATION = "wikipedia_2006_corpus/";
    public static final String ALL_WIKIPEDIA_FILE_LOCATION = "all_wikipedia/";

    public static void main(String[] args) throws IOException {
        WordCountTest wct = new WordCountTest();
        wct.run();
    }

    private void run() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("springContext.xml");

        WordCountGenerator wordCountGenerator = context.getBean("wordCountGenerator", WordCountGenerator.class);

        Resource resourceOne = new ClassPathResource(ALL_WIKIPEDIA_FILE_LOCATION);
        resourceOne.getFile().listFiles();
        File[] files = resourceOne.getFile().listFiles();
        //new String[]{"englishText_30000_40000", "englishText_40000_50000", "englishText_50000_60000", "englishText_60000_70000"};
        List<Map<String, AtomicInteger>> wordCounts = new ArrayList<Map<String, AtomicInteger>>();

        for(File file: files){

            Resource resource = new ClassPathResource(ALL_WIKIPEDIA_FILE_LOCATION + file.getName());

            wordCounts.add(wordCountGenerator.generateWordCount(resource.getInputStream()));
            System.out.println("Sent one out ... " + file.getName());
        }
        System.out.println("Finished sending out all");

        for (Map<String, AtomicInteger> wordCount : wordCounts) {
            System.out.println("Values: " + wordCount.size());
        }
        System.out.println("Finished all!!");
        ((ConfigurableApplicationContext) context).close();

    }

    public int getTokenCount(){
        return 0;
    }

}
