import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import edu.buffalo.cse.cse605.project2.Futurable;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;


public class OpenNlpProcessor
{
	public SentenceModel loadSentenceModel() throws InvalidFormatException, IOException
	{
		InputStream modelIn = getClass().getResourceAsStream("/en-sent.bin");
		final SentenceModel sentenceModel = new SentenceModel(modelIn);
		modelIn.close();
		return sentenceModel;
	}
	
	public Tokenizer loadTokenizer() throws InvalidFormatException, IOException 
	{
		InputStream modelIn = getClass().getResourceAsStream("/en-token.bin");
		final TokenizerModel tokenizerModel = new TokenizerModel(modelIn);
		modelIn.close();
		return new TokenizerME(tokenizerModel);
	}
	
	@Futurable
	public Map<String, String[]> processDocument(File file) throws InvalidFormatException, IOException
	{
		Map<String, String[]> tokenMap = new HashMap<String, String[]>();
		String documentText = FileUtils.readFileToString(file);
		
		SentenceDetector sentenceDetector = new SentenceDetectorME(loadSentenceModel());
		String[] sentences = sentenceDetector.sentDetect(documentText);
		
		Tokenizer tokenizer = loadTokenizer();
		
		int sentenceNumber = 0;
		for(String sentence : sentences)
		{
			String tokens[] = tokenizer.tokenize(sentence);
			sentenceNumber++;
			tokenMap.put(Integer.toString(sentenceNumber), tokens);
		}
		return tokenMap;
	}
}
