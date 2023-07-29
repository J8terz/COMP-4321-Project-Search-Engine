package Web_Crawler_Indexer;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.htmlparser.beans.StringBean;
import Web_Crawler_Indexer.keyword_utils.*;
import org.htmlparser.beans.LinkBean;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.MalformedURLException;


public class WebCrawler{
    Indexer indexer;
    //Methods
    // 
    /**
     * @desc Retrieves all text in a {URL} url and returns a HashMap List containing all the stemmed retrieved text
     * and their frequencies
     * @param {String} url
     * @return {Vector<String>} extractedStrings 
     */
    public HashMap<String, Integer> extractStrings(URL url) {
        StringBean stringbean = new StringBean();
        StopStem stopStem = new StopStem();
        HashMap<String, Integer> extractedStrings = new HashMap<>();
        stringbean.setURL(url.toString());
        String allText = stringbean.getStrings();
        String[] allTextArray = allText.split(" ");
        for(String text : allTextArray) {
            String fixedText = stopStem.stem(text);
            if(!stopStem.getStopWords().contains(fixedText)
                    && !fixedText.isBlank()) {
                    if(!extractedStrings.containsKey(fixedText)) {
                        extractedStrings.put(fixedText, 1);
                    }
                    else {
                        extractedStrings.put(fixedText, extractedStrings.get(fixedText)+1);
                    }
            }
        }
        return extractedStrings;
    }
    
    /**
     * @desc Retrieves all text in the {URL} url content and returns a HashMap 
     * List containing the {Integer} Position and {String} stemmed Text
     * (Also skips stopwords)
     * @param {URL} url
     * @return {HashMap<Integer, String>} extractedStringsWithPos
     */
    public HashMap<Integer, String> extractStringsWithPos(URL url) {
        StringBean stringbean = new StringBean();
        StopStem stopStem = new StopStem();
        HashMap<Integer, String> extractedStringsWithPos = new HashMap<>();
        Vector<String> allTextArrayStemmed = new Vector<>();
        stringbean.setURL(url.toString());
        String allText = stringbean.getStrings();
        String[] allTextArray = allText.split(" |\n");
      for(String text : allTextArray) {
          allTextArrayStemmed.add(stopStem.stem(text));
      }
      int pos = 0;
      for(int i=0; i<allTextArrayStemmed.size(); i++) {
          String text = allTextArrayStemmed.get(i);
          String fixedText = text.replaceAll("[^A-Za-z0-9]", "");
          if(!stopStem.getStopWords().contains(fixedText)
                  && !fixedText.isBlank()) {
                  extractedStringsWithPos.put(pos, fixedText);
                  pos++;
          }
      }
        return extractedStringsWithPos;
    }
    
    /**
     * @desc Retrieves all text in the {URL} url title and returns a HashMap 
     * List containing the {Integer} Position and {String} stemmed Text
     * (Also skips stopwords)
     * @param {URL} url
     * @return {HashMap<Integer, String>} extractedStringsWithPos
     */
    public HashMap<Integer, String> extractTitleStringsWithPos(URL url) {
        StopStem stopStem = new StopStem();
        HashMap<Integer, String> extractedStringsWithPos = new HashMap<>();
        Vector<String> allTextArrayStemmed = new Vector<>();
        String allText = this.getURLPageTitle(url);
        String[] allTextArray = allText.split(" |\n");
        
      for(String text : allTextArray) {
          allTextArrayStemmed.add(stopStem.stem(text));
      }
      int pos = 0;
      for(int i=0; i<allTextArrayStemmed.size(); i++) {
          String text = allTextArrayStemmed.get(i);
          String fixedText = text.replaceAll("[^A-Za-z0-9]", "");
          if(!stopStem.getStopWords().contains(fixedText)
                  && !fixedText.isBlank()) {
                  extractedStringsWithPos.put(pos, fixedText);
                  pos++;
          }
      }
        return extractedStringsWithPos;
    }
    
    /**
     * @desc  Adds the extracted words from a URL to a database for future query calls 
     * @param {Crawler} crawler
     * @param {URL} url
     * @param {Indexer} indexer
     * @return {HashMap<String, HashMap<URL, Integer>>} keywordIndex
     * @throws IOException
     */
    public HashMap<String, HashMap<URL, Integer>> processKeyWord(WebCrawler crawler, URL url, Indexer indexer) throws IOException{
            HashMap<String, HashMap<URL, Integer>> keywordIndex = new  HashMap<String, HashMap<URL, Integer>>();
            HashMap<String, Integer> allTextsAndFreq = crawler.extractStrings(url);
            for(Map.Entry<String, Integer> entry : allTextsAndFreq.entrySet()) {
                HashMap<URL, Integer> wordFreqInURL = new HashMap<URL, Integer>();
                wordFreqInURL.put(url, entry.getValue());
                indexer.addKeyWordEntry(entry.getKey(), wordFreqInURL);
            }
            return keywordIndex;
    }
    
    /*
     * @desc  Adds the extracted words from the URL content to a database for future query calls 
     * @param {Crawler} crawler
     * @param {URL} url
     * @param {Indexer} indexer
     * @return {HashMap<String, HashMap<URL, Integer>>} keywordIndex
     * @throws IOException
     */
    public HashMap<String, HashMap<URL, Vector<Integer>>> processKeyWordWithPos(WebCrawler crawler, URL url, Indexer indexer) throws IOException{
            HashMap<String, HashMap<URL, Vector<Integer>>> keywordIndex = new HashMap<>();
            HashMap<Integer, String> allPosAndText = crawler.extractStringsWithPos(url);
            for(Map.Entry<Integer, String> entry : allPosAndText.entrySet()) {
                indexer.addkeyWordsWithPos(entry.getValue(), entry.getKey(), url);
            }
            return keywordIndex;
    }
    
    /*
     * @desc  Adds the extracted words from the URL title to a database for future query calls 
     * @param {Crawler} crawler
     * @param {URL} url
     * @param {Indexer} indexer
     * @return {HashMap<String, HashMap<URL, Integer>>} keywordIndex
     * @throws IOException
     */
    public HashMap<String, HashMap<URL, Vector<Integer>>> processTitleKeyWordWithPos(WebCrawler crawler, URL url, Indexer indexer) throws IOException{
            HashMap<String, HashMap<URL, Vector<Integer>>> keywordIndex = new HashMap<>();
            HashMap<Integer, String> allPosAndText = crawler.extractTitleStringsWithPos(url);
            for(Map.Entry<Integer, String> entry : allPosAndText.entrySet()) {
                indexer.addTitleKeyWordsWithPos(entry.getValue(), entry.getKey(), url);
            }
            return keywordIndex;
    }
    
    
    /**
     * @desc Checks the URL for any redirections or error by checking the response code 
     * and returning the final URL for redirection or null if there is an error.
     * @param {URL} url
     * @return {URL} or null
     * @throws MalformedURLException
     * @throws IOException
     * @credit method obtained from www.java2s.com
     */
    public URL getRedirectedUrl(String url) throws MalformedURLException, IOException {
        try {
            HttpURLConnection connection;
            String finalUrl = url;  
            do {
                connection = (HttpURLConnection) new URL(finalUrl).openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode >= 300 && responseCode < 400) {
                    String redirectedUrl = connection.getHeaderField("Location");
                    if (redirectedUrl == null)
                        break;
                    finalUrl = redirectedUrl;
                } else
                    break;
            } while (connection.getResponseCode() != HttpURLConnection.HTTP_OK);
            HttpURLConnection connectionFinal = (HttpURLConnection) new URL(finalUrl).openConnection();
            connection.disconnect();
            if(connectionFinal.getResponseCode()>=400) {
                connectionFinal.disconnect();
                return null;
            }
            connectionFinal.disconnect();  
            return new URL(finalUrl.replaceAll(" ", "%20"));
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
       
    }
    
    /**
     * @desc Extracts the child URL links from the given URL
     * @param {URL} url
     * @return {Vector<URL>} extractedLinks
     */
    public Vector<URL> extractURLLinks(URL url){
        LinkBean lb = new LinkBean();
        Vector<URL> extractedLinks = new Vector<>();
        lb.setURL(url.toString());
        URL[] allLinks = lb.getLinks();
        for(URL link : allLinks) {
            extractedLinks.add(link);
        }
        return extractedLinks;
    }
    
    /**
     * @desc Extracts the parent URL links from the given URL and inputs them directly into the index
     * @param {URL} url
     * @return {Vector<URL>} extractedLinks
     * @throws IOException 
     */
    public void extractURLParentLinks(URL url, Indexer index) throws IOException{
        LinkBean lb = new LinkBean();
        lb.setURL(url.toString());
        URL[] allLinks = lb.getLinks();
        for(URL link : allLinks) {
            Vector<URL> parentLinks = new Vector<>();
            if(index.getParentLinkEntry(link) == null) {
                parentLinks.add(url);
                index.addparentLinks(link, parentLinks);
            } else {
                parentLinks = index.getParentLinkEntry(link);
                if(!parentLinks.contains(url)) {
                    parentLinks.add(url);
                    index.addparentLinks(link, parentLinks);
                }
            }
           
        }
    }
      
    /**
     * @desc  Gets a specific number of links for indexing
     * @param {URL} mainURL
     * @param {Crawler} crawler
     * @param {Vector<URL>} numberOfLinks
     * @return
     */
    public Vector<URL> getNumberOfURLLinks(URL mainURL, int numberOfLinks) {
        try {
            Vector<URL> mainUrlLinks = this.extractURLLinks(mainURL);
            Vector<URL> urlLinks = new Vector<>();         
            urlLinks.add(mainURL); 
            while(urlLinks.size() < numberOfLinks) {
                for(URL link : mainUrlLinks) {
                    if(urlLinks.size()>numberOfLinks-1) {
                        break;
                    }
                    if(!urlLinks.contains(link) && this.getRedirectedUrl(link.toString()) != null) {
                        urlLinks.add(link);
                    }
                }
                for(URL link : mainUrlLinks) {
                    if(urlLinks.size()>numberOfLinks-1) {
                        break;
                    }
                    for(URL subLink : this.extractURLLinks(link)) {
                        if(urlLinks.size()>numberOfLinks-1) {
                            break;
                        }
                        if(!urlLinks.contains(subLink) && this.getRedirectedUrl(subLink.toString()) != null) {
                            urlLinks.add(this.getRedirectedUrl(subLink.toString()));
                        }
                    }
                }
            }
            return urlLinks;
        } catch (IOException e) {
            System.out.println("WebCrwaler.getNumberOfURLlinks IOException");
            return null;
        }
    
    }

    /**
     * @desc Retrieves URL title or null if URL has no title
     * @param {URL} url
     * @return {String} doc.titl - title of the URL or null
     */
    public String getURLPageTitle(URL url) {
        try {
            Document doc = Jsoup.connect(url.toString()).get();
            return doc.title();
        } catch (IOException e) {
            System.out.println("WebCrwaler.getPageTitle IOException");
            return null;
        }
    }
    
    /**
     * @desc Retrieves URL last modified date. Retrieves current date if URL has no last modified date
     * @param {URL} url
     * @return {String} new Date(lastMod).toString() - converts the result to {Date} type then to {String}
     */
    public String getURLLastModifiedDate(URL url) {
        
        try {
            Long lastMod = url.openConnection().getLastModified();
            if(lastMod == 0) {
                return new Date(url.openConnection().getDate()).toString();
            }
            else {
                return new Date(lastMod).toString();
            }
        } catch (IOException e) {
            System.out.println("WebCrwaler.getLastModifiedDate IOException");
            return null;
        }
    }
    
    /**
     * @desc Retrieves URL page size. Retrieves number of text if URL has no page size 
     * @param {URL} url
     * @param {WebCrawler} crawler
     * @return {String} Integer.toString(size) - converts the output {Integer} size to string
     */
    public String getURLSize(URL url, WebCrawler crawler) {
        try {
            int size = url.openConnection().getContentLength();
            if(size == -1) {
                return this.getNumOfCharacters(url)+ " (Number of Characters)";
            }
            else {
                return Integer.toString(size);
            }
        } catch (IOException e) {
            System.out.println("WebCrwaler.getURLsize IOException");
            return null;
        }
    }
  
    /**
     * @desc Gets the number of characters in a URL
     * @param {URL} url
     * @return {String} totalNumOfChar
     */
    public String getNumOfCharacters(URL url) {
        Integer totalNumOfChar = 0;
        StringBean stringbean = new StringBean();
        stringbean.setURL(url.toString());
        
        String[] strings = stringbean.getStrings().split(" ");
        for(String string : strings) {
            totalNumOfChar += string.length();
        }
        return Integer.toString(totalNumOfChar);
    }
    
    /**
     * @desc method that runs the web crawler program - takes the {URL} mainURL and crawls through 30 web pages from it.
     *       It will always crawl through web pages if run regardless whether a database is stored or not.
     *       URLs crawled through and needed information from it are stored in the {Indexer} urlIndexer. 
     * {String} mainURLString input
     * @param {URL} mainURLString
     * @throws InterruptedException
     * @throws IOException
     */
    public static void process(String mainURLString) throws InterruptedException, IOException{
        try{
            URL mainURL = new URL(mainURLString);
            WebCrawler crawler = new WebCrawler();
            Indexer urlIndexer = new Indexer("Final", "keyWords", "pageName", 
                    "lastModifiedDate", "pageSize", "childLinks"
                    , "KeyWordsWithPos", "titleKeywordsWithPos"
                    , "parentLinks");
                urlIndexer.createDatabase();
            crawler.extractStringsWithPos(mainURL);
            Vector<URL> urlLinks = crawler.getNumberOfURLLinks(mainURL, 300);
            
            int urlPortion = 50;
            ExecutorService service = Executors.newFixedThreadPool(20); 
            int threadNum =0;
            Vector<URL> urlLinksPortion = new Vector<>();
            for(int i=0; i<300; i++) {
                urlLinksPortion.add(urlLinks.get(i));
                if(urlLinksPortion.size()==urlPortion || i>=300) {
                    Multithread multithread = new Multithread(urlIndexer, urlLinksPortion, crawler, threadNum);
                    service.execute(multithread);               
                    urlLinksPortion = new Vector<>();
                    threadNum++;
                }
            }
            service.shutdown();
            while(!service.awaitTermination(2, TimeUnit.MINUTES))  {       
               }
            urlIndexer.finalize();
            
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Main IOException");
            }
    }

 /**
 * @throws InterruptedException 
 * @desc main thread used for the "process" method testing
 */
public static void main(String[] args) throws InterruptedException {
       try{
           WebCrawler.process("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm"); 
       } catch (IOException e) {
           e.printStackTrace();
           System.out.println("Main IOException");
       }
   }
}