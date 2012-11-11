import edu.buffalo.cse.cse605.project2.Futureable;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountGenerator {

    @Futureable
    public Map<String, AtomicInteger> generateWordCount(InputStream inputStream) throws IOException {

        StringWriter writer = new StringWriter();
        IOUtils.copy(inputStream, writer, "UTF-8");
        String inputText= writer.toString();

        Map<String, AtomicInteger> returnMap = new HashMap<String, AtomicInteger>();

        inputText = inputText.replaceAll("\\p{P}", " ");
        inputText = inputText.toLowerCase();

        for (String chunk : inputText.split(" ")) {
            chunk = chunk.trim();
            if (chunk.length() == 0)
                continue;

            AtomicInteger integer = returnMap.get(chunk);

            if (integer == null) {
                integer = new AtomicInteger(0);
                returnMap.put(chunk, integer);
            }

            integer.incrementAndGet();
        }

        return returnMap;
    }
}
