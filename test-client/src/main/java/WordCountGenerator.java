import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import edu.buffalo.cse.cse605.project2.Futureable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WordCountGenerator {

    @Futureable
    public Map<String, AtomicInteger> generateWordCount(final InputStream inputStream) throws IOException {

        InputSupplier inputSupplier = new InputSupplier<InputStream>() {
            public InputStream getInput() throws IOException {
                return inputStream;
            }
        };

        return CharStreams.readLines(inputSupplier, new WordCountLineProcessor());
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
