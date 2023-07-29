package Web_Crawler_Indexer;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

public class MultiThread2 implements Runnable{
    
    //fields
    FileExporter exporter;
    HashMap<URL, String> urlData;
    Vector<URL> urlPortion;
    
    //constructor
    public MultiThread2(Vector<URL> urlPortion, HashMap<URL, String> urlData, FileExporter exporter) {
        this.urlPortion=urlPortion;
        this.exporter = exporter;
        this.urlData=urlData;
        
    }
    
    //synchronized method
    public  synchronized void processURL() throws IOException{
        
        for(URL url: urlPortion) {
            
            synchronized (urlData) {
                urlData.put(url, exporter.organizedURLData(url));
            }
        }
       
    }
    
   //implementation of the run method
   //for multithreading
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