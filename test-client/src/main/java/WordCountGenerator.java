import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import edu.buffalo.cse.cse605.project2.Futurable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountGenerator {

    @Futurable
    public Map<String, AtomicInteger> generateWordCount(final File inputStream) throws IOException {

        return CharStreams.readLines(Files.newReaderSupplier(inputStream, Charset.defaultCharset()), new WordCountLineProcessor());
    }


    private class WordCountLineProcessor implements LineProcessor<Map<String, AtomicInteger>> {

        private Map<String, AtomicInteger> resultingMap = new HashMap<String, AtomicInteger>();

        @Override
        public boolean processLine(String inputText) throws IOException {
            inputText = inputText.replaceAll("\\p{P}", " ");
            inputText = inputText.toLowerCase();

            for (String chunk : inputText.split(" ")) {
                chunk = chunk.trim();
                if (chunk.length() == 0)
                    continue;

                AtomicInteger integer = resultingMap.get(chunk);

                if (integer == null) {
                    integer = new AtomicInteger(0);
                    resultingMap.put(chunk, integer);
                }

                integer.incrementAndGet();
            }

            return true;
        }

        @Override
        public Map<String, AtomicInteger> getResult() {
            return resultingMap;
        }
    }
}
