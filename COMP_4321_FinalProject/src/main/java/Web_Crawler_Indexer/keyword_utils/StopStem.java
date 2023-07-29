package Web_Crawler_Indexer.keyword_utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * @desc stopword implementation for word retrieval. Prevents unwanted stopwords and words to similar to each other from being stored. 
 *
 */
public class StopStem
{
	private Porter porter;
	private HashSet<String> stopWords;
	public boolean isStopWord(String str)
	{
		return stopWords.contains(str);	
	}
	public StopStem()
	{
		super();
		porter = new Porter();
		stopWords = new HashSet<String>();
		//File stopFile = new File("/src/main/java/Web_Crawler_Indexer/keyword_utils/stopwords.txt");
		File stopFile = new File("stopwords.txt");
		Scanner scanner;
            try {
                scanner = new Scanner(stopFile);
                while(scanner.hasNext()) {
                    stopWords.add(stem(scanner.next()));
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			
	}
	public String stem(String str)
	{
		return porter.stripAffixes(str);
	}
	
	public HashSet<String> getStopWords(){
	    return stopWords;
	}
}
