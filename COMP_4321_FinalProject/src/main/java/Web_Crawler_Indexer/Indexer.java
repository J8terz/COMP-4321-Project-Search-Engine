package Web_Crawler_Indexer;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.util.HashMap;
import java.net.URL;
import java.util.Vector;
import java.util.Collections;


public class Indexer{
    private RecordManager recman;
    private HTree keyWords;
    private HTree pageName;
    private HTree lastModifiedDate;
    private HTree pageSize;
    private HTree childLinks;
    private HTree keywordsWithPos;
    private HTree titleKeywordsWithPos;
    private HTree parentLinks;
    private Boolean savedDatabase=false;
    
    /**
     * @desc Constructor of the class Indexer. Creates a {RecordManager} recman that stores the database of HTrees
     * then checks if any HTree with the given names exist. If so, get those HTrees, else create new HTrees with
     * the given names in the arguments
     * @param {String} recordManagerName
     * @param {String} keyWordsHtName
     * @param {String} pageNameHtName
     * @param {String} lastModifiedHtName
     * @param {String} pageSizeHtName
     * @param {String} childLinksHtName
     * @param {Boolean} savedDatabse - set to true if save database is found, else its false
     * @throws IOException
     */
    public Indexer(String recordManagerName, String keyWordsHtName, String pageNameHtName
            , String lastModifiedHtName, String pageSizeHtName, String childLinksHtName
            , String keywordsWithPosHtName, String titleKeywordsWithPosHtName
            ,String parentLinksHtName) throws IOException {
        this.recman = RecordManagerFactory.createRecordManager(recordManagerName);
        long keyWordrecID = recman.getNamedObject(keyWordsHtName);
        long pageNamerecID = recman.getNamedObject(pageNameHtName);
        long lastModifiedrecID = recman.getNamedObject(lastModifiedHtName);
        long pageSizerecID = recman.getNamedObject(pageSizeHtName);
        long childLinksrecID = recman.getNamedObject(childLinksHtName);
        long keywordsWithPosrecID = recman.getNamedObject(keywordsWithPosHtName);
        long titleKeywordsWithPosrecID = recman.getNamedObject(titleKeywordsWithPosHtName);
        long parentLinksrecID = recman.getNamedObject(parentLinksHtName);
        
        if(keyWordrecID != 0) {
            keyWords = HTree.load(recman, keyWordrecID);
            pageName = HTree.load(recman, pageNamerecID);
            lastModifiedDate = HTree.load(recman, lastModifiedrecID);
            pageSize = HTree.load(recman, pageSizerecID);
            childLinks = HTree.load(recman, childLinksrecID);
            keywordsWithPos = HTree.load(recman, keywordsWithPosrecID);
            titleKeywordsWithPos = HTree.load(recman, titleKeywordsWithPosrecID);
            parentLinks = HTree.load(recman, parentLinksrecID);
            recman.setNamedObject("keyWords", keyWords.getRecid());
            recman.setNamedObject("pageName", pageName.getRecid());
            recman.setNamedObject("lastModifiedDate", lastModifiedDate.getRecid());
            recman.setNamedObject("pageSize", pageSize.getRecid());
            recman.setNamedObject("childLinks", childLinks.getRecid());
            recman.setNamedObject("KeyWordsWithPos", keywordsWithPos.getRecid());
            recman.setNamedObject("titleKeywordsWithPos", titleKeywordsWithPos.getRecid());
            recman.setNamedObject("parentLinks", parentLinks.getRecid());
            savedDatabase = true;
        } 
    }
    
    //Methods
    /**
     * @desc Creates the databases of the indexer
     * (Only run if no database is found)
     */
    public void createDatabase() {
        try {
            keyWords = HTree.createInstance(recman);
            pageName = HTree.createInstance(recman);
            lastModifiedDate = HTree.createInstance(recman);
            pageSize = HTree.createInstance(recman);
            childLinks = HTree.createInstance(recman);
            keywordsWithPos = HTree.createInstance(recman);
            titleKeywordsWithPos = HTree.createInstance(recman);
            parentLinks = HTree.createInstance(recman);
            recman.setNamedObject("keyWords", keyWords.getRecid());
            recman.setNamedObject("pageName", pageName.getRecid());
            recman.setNamedObject("lastModifiedDate", lastModifiedDate.getRecid());
            recman.setNamedObject("pageSize", pageSize.getRecid());
            recman.setNamedObject("childLinks", childLinks.getRecid());
            recman.setNamedObject("KeyWordsWithPos", keywordsWithPos.getRecid());
            recman.setNamedObject("titleKeywordsWithPos", titleKeywordsWithPos.getRecid());
            recman.setNamedObject("parentLinks", parentLinks.getRecid());
        } catch (IOException e) {
            System.out.println("CreateDatabase IOException");
        }
    }
     
    /**
     * @desc Getter for savedDatabase field
     * @return {Boolean} savedDatabse
     */
    public Boolean checkForSavedDatabase() {
        return savedDatabase;
    }
       
    /**
     *@desc saves changes to the indexer database and closes it
     */
    public void finalize() throws IOException{
        recman.commit();
        recman.close();
    }
    
    /**
     * @desc  adds a keyword entry into the {HTree} keyWords with the keyword as the HTree key and a 
     * {HashMap<String, Integer>} (containing a {URL} as a a key and the frequency of the word in the URL {Integer} as the value)
     * as the value. If the HTree already contains the word, appends its {HashMap<String, Integer>} value with the HashMap to input
     * @param {String} keyword
     * @param {HashMap<URL, Integer>} numOfWordsInURL
     * @throws IOException
     */
    public void addKeyWordEntry(String keyword, HashMap<URL, Integer> numOfWordsInURL) throws IOException{  
        
        if(keyWords.get(keyword) == null) {       
            keyWords.put(keyword, numOfWordsInURL);
        }
        else {
            @SuppressWarnings("unchecked")
            HashMap<URL, Integer> storedContent = (HashMap<URL, Integer>) keyWords.get(keyword);
            storedContent.putAll(numOfWordsInURL);
            keyWords.put(keyword, storedContent);
        }
    }
    
    /**
     * Indexer Setters for {HTree} pageName
     */
    public void addPageNameEntry(URL url, String pageNameText) throws IOException{    
        pageName.put(url, pageNameText);
    }
    /**
     * Indexer Setters for {HTree} lastModifiedDate
     */
    public void addLastModifiedEntry(URL url, String lastModDate) throws IOException{  
            lastModifiedDate.put(url, lastModDate);   
    }
    /**
     * Indexer Setters for {HTree} pageSize
     */
    public void addPageSizeEntry(URL url, String pageSizeText) throws IOException{
            pageSize.put(url, pageSizeText);
    }
    /**
     * Indexer Setters for {HTree} childLinks
     */
    public void addChildLinkEntry(URL url, Vector<URL> childLink) throws IOException{
        childLinks.put(url, childLink);
    }
    
    public void addparentLinks(URL url , Vector<URL> parentLink) throws IOException{
        parentLinks.put(url, parentLink);
    }
    
    
    /**
     * @desc Adds the {String} word and its {Integer} Pos from a {URL} url to the
     * {HTree} keywordsWithPos Index
     * @param {String} word
     * @param {Integer} pos
     * @param {URL} url
     * @throws IOException
     */
    public void addkeyWordsWithPos(String word , Integer pos, URL url) throws IOException{ //HashMap<URL, Vector<Integer>> posInURL
        @SuppressWarnings("unchecked")
        HashMap<URL, Vector<Integer>> wordPosListInURL =  (HashMap<URL, Vector<Integer>>) keywordsWithPos.get(word);
        if(wordPosListInURL == null) {      
            HashMap<URL, Vector<Integer>> posInURL = new HashMap<>();
            Vector<Integer> positions = new Vector<>();
            positions.add(pos);
            posInURL.put(url, positions);
            keywordsWithPos.put(word, posInURL);
            
        }
        else {
            if(wordPosListInURL.get(url) == null) {
                @SuppressWarnings("unchecked")
                HashMap<URL, Vector<Integer>> storedPosNewURL =  (HashMap<URL, Vector<Integer>>) keywordsWithPos.get(word);
                Vector<Integer> positions = new Vector<>();
                positions.add(pos);
                storedPosNewURL.put(url, positions);
                keywordsWithPos.put(word, storedPosNewURL);
                
                
            } else {
                @SuppressWarnings("unchecked")
                HashMap<URL, Vector<Integer>> storedPosInURL =  (HashMap<URL, Vector<Integer>>) keywordsWithPos.get(word);
                storedPosInURL.get(url).add(pos);
                keywordsWithPos.put(word, storedPosInURL);
                this.getkeywordsWithPos(word);
                
            }
            
        }
    }
    
    /**
     * @desc Adds the {String} word and its {Integer} Pos from a {URL} url to the
     * {HTree} titleKeywordsWithPos Index
     * @param word
     * @param pos
     * @param url
     * @throws IOException
     */
    public void addTitleKeyWordsWithPos(String word , Integer pos, URL url) throws IOException{ //HashMap<URL, Vector<Integer>> posInURL
        @SuppressWarnings("unchecked")
        HashMap<URL, Vector<Integer>> wordPosListInURL =  (HashMap<URL, Vector<Integer>>) titleKeywordsWithPos.get(word);
        if(wordPosListInURL == null) {      
            HashMap<URL, Vector<Integer>> posInURL = new HashMap<>();
            Vector<Integer> positions = new Vector<>();
            positions.add(pos);
            posInURL.put(url, positions);
            titleKeywordsWithPos.put(word, posInURL);
        }
        else {
            if(wordPosListInURL.get(url) == null) {
                @SuppressWarnings("unchecked")
                HashMap<URL, Vector<Integer>> storedPosNewURL =  (HashMap<URL, Vector<Integer>>) titleKeywordsWithPos.get(word);
                Vector<Integer> positions = new Vector<>();
                positions.add(pos);
                storedPosNewURL.put(url, positions);
                titleKeywordsWithPos.put(word, storedPosNewURL);
            } else {
                @SuppressWarnings("unchecked")
                HashMap<URL, Vector<Integer>> storedPosInURL =  (HashMap<URL, Vector<Integer>>) titleKeywordsWithPos.get(word);
                storedPosInURL.get(url).add(pos);
                titleKeywordsWithPos.put(word, storedPosInURL);
                this.getkeywordsWithPos(word);
            }
        }
    }

    /**
     * @desc Retrieves all words from the {HTree} KeyWords and retrieves the keywords of the specified {URL} url.
     *       It creates 2 exactly the same{Vector<Integer>}, wordFreq and wordFreqSorted and 1 {String<String>} words. The wordFreqSorted is
     *       sorted and modified to only have the highest frequencies of words in the URL, then the index of those highest frequencies
     *       in the {Vector<Integer>} wordFreq is taken and used to extract the keywords from {Vector<Integer>} wordFreqs and {String<String>} words
     *       After extraction of the keyword and its frequency, it is removed from the array to prevent re-retrieval.     
     * @param {URL} url
     * @return {Vector<Vector<?>>} keywordArrays
     * @throws IOException
     */
    public Vector<Vector<?>> getKeywords(URL url) {
        try {
            Vector<String> words = new Vector<>();
            Vector<Integer> wordFreq = new Vector<>();
            Vector<Integer> sortedWordFreq = new Vector<>();
            Vector<String> keywordsArray = new Vector<>();
            Vector<Integer> keywordsFreqArray = new Vector<>();
            Vector<Vector<?>> keywordArrays = new Vector<>();
            FastIterator iter = keyWords.keys();
            String word = (String) iter.next();
            while(word!=null) {
                @SuppressWarnings("unchecked")
                HashMap<URL, Integer> keyWordsHtvalue = (HashMap<URL, Integer>) keyWords.get(word);
                if(keyWordsHtvalue.get(url) != null) {
                    words.add(word);
                    wordFreq.add(keyWordsHtvalue.get(url));
                    sortedWordFreq.add(keyWordsHtvalue.get(url));
                }
                word = (String) iter.next();
            }
            Collections.sort(sortedWordFreq);
            Collections.reverse(sortedWordFreq);
            sortedWordFreq.setSize(10);
            for(Integer freq : sortedWordFreq) {
                if(freq==null) {
                    continue;
                }
                Integer index = wordFreq.indexOf(freq);
                keywordsArray.add(words.get(index));
                keywordsFreqArray.add(wordFreq.get(index));
                words.remove(words.get(index));
                wordFreq.remove(wordFreq.get(index));
            }
            keywordArrays.add(keywordsArray);
            keywordArrays.add(keywordsFreqArray);
            return keywordArrays; 
        } catch (IOException e) {
            System.out.println("Indexer.getKeywords retrieval IOException");
            return null;
        }
        
    }
    
    /**
     * @desc Getter for {HTree} pageName through specified {URL} url
     * @param {URL} url
     * @return {String} (String) pageName.get(url)
     */
    public String getPageNameEntry(URL url) {
        try {
            return (String) pageName.get(url);
        } catch (IOException e) {
            System.out.println("Indexer.getPageNameEntry IOException");
            return null;
        }
    }
    /**
     * @desc Getter for {HTree} lastModifiedDate through specified {URL} url
     * @param {URL} url
     * @return {String}  (String) lastModifiedDate.get(url)
     */
    public String getLastModifiedEntry(URL url) {
        try {
            return (String) lastModifiedDate.get(url);
        } catch (IOException e) {
            System.out.println("Indexer.getLastModifedEntry IOException");
            return null;
        }
    }
    
    /**
     * @desc Getter for {HTree} pageSize through specified {URL} url
     * @param {URL} url
     * @return {String} (String) pageSize.get(url)
     */
    public String getPageSizeEntry(URL url) {
        try {
            return (String) pageSize.get(url);
        } catch (IOException e) {
            System.out.println("Indexer.getPageSizeEntry IOException");
            return null;
        }
    }
    
    /**
     * @desc Getter for {HTree} childLinks through specified {URL} url
     * @param {URL} url
     * @return (Vector<URL>) childLinks.get(url)
     */
    @SuppressWarnings("unchecked")
    public Vector<URL> getChildLinkEntry(URL url) {
        try {
            return (Vector<URL>) childLinks.get(url);
        } catch (IOException e) {
            System.out.println("Indexer.getChildLinkEntry IOException");
            return null;
        }
    }
    
    
    /**
     * @desc Getter for {HTree} keywordsWithPos through specified {URL} url
     * @param {String} keyword
     * @return {HashMap<URL, Vector<Integer>>} 
     */
    @SuppressWarnings("unchecked")
    public HashMap<URL, Vector<Integer>> getkeywordsWithPos(String keyword) {
        try {
            return (HashMap<URL, Vector<Integer>>) keywordsWithPos.get(keyword);
        } catch (IOException e) {
            System.out.println("Indexer.getkeywordsWithPos IOException");
            return null;
        }
    }
    
    
    /**
     * @desc Getter for {HTree} titleKeywordsWithPos through specified {URL} url
     * @param {String} keyword
     * @return {HashMap<URL, Vector<Integer>>} titleKeywordsWithPos.get(keyword)
     */
    @SuppressWarnings("unchecked")
    public HashMap<URL, Vector<Integer>> getTitleKeywordsWithPos(String keyword) {
        try {
            return (HashMap<URL, Vector<Integer>>) titleKeywordsWithPos.get(keyword);
        } catch (IOException e) {
            System.out.println("Indexer.getkeywordsWithPos IOException");
            return null;
        }
    }
    
    /**
     * @desc Getter for {HTree} parentLinks through specified {URL} url
     * @param {URL} url
     * @return (Vector<URL>) childLinks.get(url)
     */
    @SuppressWarnings("unchecked")
    public Vector<URL> getParentLinkEntry(URL url) {
        try {
            return (Vector<URL>) parentLinks.get(url);
        } catch (IOException e) {
            System.out.println("Indexer.getChildLinkEntry IOException");
            return null;
        }
    }
    
    /**
     * @desc returns all the URLs that have been indexed by the {Indexer} indexer
     * @return {Vector<URL> IndexedURLs}
     */
    public Vector<URL> getIndexedURLs(){
        Vector<URL> IndexedURLs = new Vector<>();
        try {
            FastIterator iterator = pageName.keys();
            URL url = (URL) iterator.next();
            while(url!=null) {
                IndexedURLs.add(url);
                url = (URL) iterator.next();
            }
            return IndexedURLs;
            
        } catch (IOException e) {
            System.out.println("Indexer.getIndexedURLs IOException");
            return null;
        }
    }
    
}
