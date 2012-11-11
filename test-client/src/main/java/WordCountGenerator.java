import edu.buffalo.cse.cse605.project2.Futureable;
import sun.java2d.xr.MutableInteger;

import java.util.HashMap;
import java.util.Map;

public class WordCountGenerator {

    @Futureable
    public Map<String, MutableInteger> generateWordCount(String inputText) {
        Map<String, MutableInteger> returnMap = new HashMap<String, MutableInteger>();

        inputText = inputText.replaceAll("\\p{P}", " ");
        inputText = inputText.toLowerCase();

        for (String chunk : inputText.split(" ")) {
            chunk = chunk.trim();
            if (chunk.length() == 0)
                continue;

            MutableInteger integer = returnMap.get(chunk);

            if (integer == null) {
                integer = new MutableInteger(0);
                returnMap.put(chunk, integer);
            }

            integer.setValue(integer.getValue() + 1);
        }

        return returnMap;
    }
}
