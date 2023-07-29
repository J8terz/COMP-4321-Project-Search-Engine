package Web_Crawler_Indexer;

import java.net.URL;
import java.util.Vector;

public class FileExporter{
    //Fields
    //{Indexer} index to export information from. 
    String organizedDataString;
    //Constructor
    private Indexer index;
    /**
     * @param Indexer {index}
     */
    public FileExporter(Indexer index) {
        this.index = index;
    }
    
    //Methods
    /**
     * @desc Prepares the information form the index into a single String to be exported 
     * @return {String} organizedData
     */

    @SuppressWarnings("unchecked")
    public String organizedURLData (URL url) {
        StringBuilder organizedData= new StringBuilder();
        organizedData.append("Page Name: " + index.getPageNameEntry(url) + "<br/>"
                      + "URL: " + url.toString() + "<br/>"
                      + "Last Modification of page: " + index.getLastModifiedEntry(url).toString() + "<br/>"
                      + "Size of page: " + index.getPageSizeEntry(url) + "<br/>" + "keywords:  ");  
        Vector<Vector<?>> keywordArrays = index.getKeywords(url);
        Vector<String> keywordsArray = (Vector<String>) keywordArrays.get(0);
        Vector<Integer> keywordsFreqArray = (Vector<Integer>) keywordArrays.get(1);
        for(int i=0; i<keywordsArray.size(); i++) {
            organizedData.append("\""+keywordsArray.get(i) +"\""+ " - " + keywordsFreqArray.get(i) + " |");
        }
        organizedData.append("<br/>" + "Child links: " + "<br/>");
        if(index.getChildLinkEntry(url).isEmpty()) {
            organizedData.append("No child links available");
        } else {
            for(URL childURL : index.getChildLinkEntry(url)) {
                if(childURL==null) {
                    break;
                }
                organizedData.append(childURL.toString() + "<br/>");
            }
        }
        organizedData.append("<br/>" + "Parent links: " + "<br/>");
        if(index.getParentLinkEntry(url).isEmpty()) {
            organizedData.append("No Parent links available");
        } else {
            Vector<URL> sizedParentURLs = index.getParentLinkEntry(url);
            sizedParentURLs.setSize(10);
            for(URL parentURL : sizedParentURLs) {
                if(parentURL==null) {
                    break;
                }
                organizedData.append(parentURL .toString() + "<br/>");
            }
        }
        return organizedData.toString();
    }
}
