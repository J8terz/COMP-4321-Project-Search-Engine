package Web_Crawler_Indexer;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

public class Multithread implements Runnable{
    //Fields
    Indexer urlIndexer;
    Vector<URL> urlPortion;
    WebCrawler crawler;
    int threadNum;
    
    //Constructor
    public Multithread(Indexer index, Vector<URL> urlPortion, WebCrawler crawler, int threadNum) {
        this.urlIndexer=index;
        this.urlPortion = urlPortion;
        this.crawler = crawler;
        this.threadNum = threadNum;
    }
    
    /**
     * @desc Synchronizes the Web Crawling using Parallel program to
     * perform faster
     * @throws IOException
     */
    public synchronized void processURL() throws IOException{
        
        for(URL url: urlPortion) {
            
            synchronized (urlIndexer) {
                urlIndexer.addPageNameEntry(url, crawler.getURLPageTitle(url));
            }
            
            synchronized (urlIndexer) {
                urlIndexer.addPageSizeEntry(url, crawler.getURLSize(url, crawler));
            }
            
            synchronized (urlIndexer) {
                crawler.processKeyWord(crawler, url, urlIndexer);
            }
            
            synchronized (urlIndexer) {
                crawler.processKeyWordWithPos(crawler, url, urlIndexer);
            }
            
            synchronized (urlIndexer) {
                crawler.processTitleKeyWordWithPos(crawler, url, urlIndexer);
            }
            
            synchronized (urlIndexer) {
                urlIndexer.addLastModifiedEntry(url, crawler.getURLLastModifiedDate(url));
            }

            synchronized (urlIndexer) {
                Vector<URL> neededChildLinks = crawler.extractURLLinks(url);
                neededChildLinks.setSize(10);
                urlIndexer.addChildLinkEntry(url, neededChildLinks);
            }
            
            synchronized (urlIndexer) {
                crawler.extractURLParentLinks(url, urlIndexer);
            }
            
        }
       
    }
    
    /**
     *@desc implementation of the run() Function interface
     *for multithreading
     */
    public void run() {
       try {
        processURL();
       } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        System.out.println("multithread error");
       }
       return;
    }
}
